package com.zhang.netty.client;

import com.zhang.netty.client.handler.ClientHandler;
import com.zhang.netty.common.NettyDecoder;
import com.zhang.netty.common.NettyEncoder;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.LengthFieldPrepender;

import java.net.InetSocketAddress;

public class NettyClient {
    private String host;
    private int port;

    public NettyClient(String host, int port) {
        this.host = host;
        this.port = port;
    }

    public void connect() throws Exception {
        NioEventLoopGroup group = new NioEventLoopGroup();
        final LengthFieldPrepender preLength = new LengthFieldPrepender(4, false);
        final NettyDecoder decoder = new NettyDecoder();
        final NettyEncoder encoder = new NettyEncoder();
        final ClientHandler clientHandler = new ClientHandler();
        try {
            Bootstrap bs = new Bootstrap();
            bs
                    .group(group)
                    .channel(NioSocketChannel.class)
                    .remoteAddress(new InetSocketAddress(host, port))
                    .handler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel socketChannel) throws Exception {
                            socketChannel.pipeline()
                                    .addLast("preLength", preLength)
                                    .addLast("encoder", encoder)
                                    .addLast("decoder", decoder)
                                    .addLast("clientHandler", clientHandler);
                        }
                    });
            ChannelFuture cf = bs.connect().sync();
            cf.channel().closeFuture().sync();
        } finally {
            group.shutdownGracefully().sync();
        }
    }

    public static void main(String[] args) {
        String host = "127.0.0.1";
        int port = 8000;
        if (args.length >= 2) {
            host = args[0];
            port = Integer.parseInt(args[1]);
        }

        try {
            new NettyClient(host, port).connect();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
