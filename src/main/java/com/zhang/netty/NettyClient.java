package com.zhang.netty;

import com.zhang.netty.handler.ClientHandler;
import com.zhang.netty.handler.ExceptionHandler;
import com.zhang.netty.handler.NettyDecoder;
import com.zhang.netty.handler.NettyEncoder;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.codec.LengthFieldPrepender;
import io.netty.handler.timeout.IdleStateHandler;

import java.net.InetSocketAddress;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class NettyClient {
    private static final int writerIdleTime = 0;
    private static final int readerIdleTime = 0;
    private static final int allIdleTime = 30;

    private int coreThreadNum = Runtime.getRuntime().availableProcessors() * 2;
    private int maxThreadNum = 64;
    private int queueNum = 64;
    private String threadName = "zhang";

    private String host;
    private int port;

    public NettyClient(String host, int port, String threadName) {
        this.host = host;
        this.port = port;
        this.threadName = threadName;
    }

    public void connect() throws Exception {
        NioEventLoopGroup group = new NioEventLoopGroup();
        ExecutorService processGroup = new ThreadPoolExecutor(coreThreadNum, maxThreadNum, 30L, TimeUnit.SECONDS,
                new LinkedBlockingDeque<>(queueNum), r -> new Thread(r, "NettyClient-" + threadName + "-Processor"));

        final ExceptionHandler exceptionHandler = new ExceptionHandler();
        final LengthFieldPrepender preLength = new LengthFieldPrepender(4, false);
        final NettyDecoder decoder = new NettyDecoder();
        final NettyEncoder encoder = new NettyEncoder();
        final ClientHandler clientHandler = new ClientHandler(processGroup);
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
                                    .addLast(new LengthFieldBasedFrameDecoder(Integer.MAX_VALUE, 0, 4, 0, 0))
                                    .addLast("decoder", decoder)
                                    .addLast(new IdleStateHandler(readerIdleTime, writerIdleTime, allIdleTime, TimeUnit.SECONDS))
                                    .addLast("clientHandler", clientHandler)
                                    .addLast("exceptionHandler", exceptionHandler);
                        }
                    });
            ChannelFuture cf = bs.connect().sync();
            cf.channel().closeFuture().sync();
        } finally {
            group.shutdownGracefully();
        }
    }

    public static void main(String[] args) {
        String host = "127.0.0.1";
        int port = 8000;
        String name = "zhang";

        if (args.length >= 2) {
            host = args[0];
            port = Integer.parseInt(args[1]);
            if (args.length >= 3) {
                name = args[2];
            }
        }

        NettyClient nettyClient = new NettyClient(host, port, name);
        try {
            nettyClient.connect();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
