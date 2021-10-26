package com.zhang.netty.server;

import com.zhang.netty.common.NettyDecoder;
import com.zhang.netty.common.NettyEncoder;
import com.zhang.netty.server.handler.ServerHandler;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.codec.LengthFieldPrepender;
import io.netty.handler.timeout.IdleStateHandler;
import lombok.extern.slf4j.Slf4j;

import java.net.InetSocketAddress;
import java.util.concurrent.TimeUnit;

@Slf4j
public class NettyServer {
    private int writerIdleTime = 0;
    private int readerIdleTime = 0;
    private int allIdleTime = 60;

    private int port;

    public NettyServer(int port) {
        this.port = port;
    }

    public void serve() throws Exception {
        NioEventLoopGroup group = new NioEventLoopGroup();
        final LengthFieldPrepender preLength = new LengthFieldPrepender(4, false);
        final NettyDecoder decoder = new NettyDecoder();
        final NettyEncoder encoder = new NettyEncoder();
        final ServerHandler serverHandler = new ServerHandler();
        try {
            ServerBootstrap sbs = new ServerBootstrap();
            sbs
                    .group(group)
                    .channel(NioServerSocketChannel.class)
                    .localAddress(new InetSocketAddress(port))
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel socketChannel) throws Exception {
                            socketChannel.pipeline()
                                    .addLast("preLength", preLength)
                                    .addLast("encoder", encoder)
                                    .addLast(new LengthFieldBasedFrameDecoder(Integer.MAX_VALUE, 0, 4, 0, 0))
                                    .addLast("decoder", decoder)
                                    .addLast(new IdleStateHandler(readerIdleTime, writerIdleTime, allIdleTime, TimeUnit.SECONDS))
                                    .addLast("serverHandler", serverHandler);
                        }
                    });
            ChannelFuture cf = sbs.bind().sync();
            System.out.println(NettyServer.class.getName() + " started and listen on " + cf.channel().localAddress());
            cf.channel().closeFuture().sync();
        } finally {
            group.shutdownGracefully().sync();
        }
    }

    public static void main(String[] args) {
        int port = 8000;
        if (args.length >= 1) {
            port = Integer.parseInt(args[0]);
        }
        NettyServer ns = new NettyServer(port);
        try {
            ns.serve();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
