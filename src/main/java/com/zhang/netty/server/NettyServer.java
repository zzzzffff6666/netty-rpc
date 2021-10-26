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
import io.netty.handler.codec.LengthFieldPrepender;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.net.InetSocketAddress;

public class NettyServer {
    private static final Logger log = LogManager.getLogger(NettyServer.class);

    private int port;

    public NettyServer(int port) {
        this.port = port;
    }

    public void serve() throws Exception {
        log.debug("Server start ******** DEBUG");
        log.info("Server start ******** INFO");
        log.error("Server start ******** ERROR");
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
                                    .addLast("decoder", decoder)
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
