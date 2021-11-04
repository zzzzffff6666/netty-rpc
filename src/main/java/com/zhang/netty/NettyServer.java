package com.zhang.netty;

import com.zhang.netty.handler.ExceptionHandler;
import com.zhang.netty.handler.NettyDecoder;
import com.zhang.netty.handler.NettyEncoder;
import com.zhang.netty.handler.ServerHandler;
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
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

@Slf4j
public class NettyServer {
    private static final int writerIdleTime = 0;
    private static final int readerIdleTime = 0;
    private static final int allIdleTime = 60;

    private int coreThreadNum = Runtime.getRuntime().availableProcessors() * 2;
    private int maxThreadNum = 64;
    private int queueNum = 64;
    private String threadName = "zhang";

    private int port;

    public NettyServer(int port, String threadName) {
        this.port = port;
        this.threadName = threadName;
    }

    public void serve() throws Exception {
        NioEventLoopGroup bossGroup = new NioEventLoopGroup(1);
        NioEventLoopGroup workerGroup = new NioEventLoopGroup();
        ExecutorService processGroup = new ThreadPoolExecutor(coreThreadNum, maxThreadNum, 30L, TimeUnit.SECONDS,
                new LinkedBlockingDeque<>(queueNum), r -> new Thread(r, "NettyServer-" + threadName + "-Processor"));

        final ExceptionHandler exceptionHandler = new ExceptionHandler();
        final LengthFieldPrepender preLength = new LengthFieldPrepender(4, false);
        final NettyDecoder decoder = new NettyDecoder();
        final NettyEncoder encoder = new NettyEncoder();
        final ServerHandler serverHandler = new ServerHandler(processGroup);
        try {
            ServerBootstrap sbs = new ServerBootstrap();
            sbs
                    .group(bossGroup, workerGroup)
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
                                    .addLast("serverHandler", serverHandler)
                                    .addLast("exceptionHandler", exceptionHandler);
                        }
                    });
            ChannelFuture cf = sbs.bind().sync();
            log.info("NettyServer start and listen on {}", cf.channel().localAddress());
            cf.channel().closeFuture().sync();
        } finally {
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }
    }

    public static void main(String[] args) {
        int port = 8000;
        String name = "zhang";

        if (args.length >= 1) {
            port = Integer.parseInt(args[0]);
            if (args.length >= 2) {
                name = args[1];
            }
        }

        NettyServer ns = new NettyServer(port, name);
        try {
            ns.serve();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
