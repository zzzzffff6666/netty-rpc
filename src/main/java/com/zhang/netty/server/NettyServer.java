package com.zhang.netty.server;

import com.zhang.netty.config.ConfigLoader;
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
    private int port;

    private int coreThreadNum;
    private int maxThreadNum;
    private int queueNum;
    private String threadName;

    private int writerIdleTime;
    private int readerIdleTime;
    private int allIdleTime;

    public NettyServer() {
        port = ConfigLoader.getInstance().getPort();
        threadName = ConfigLoader.getInstance().getThreadName();
        coreThreadNum = ConfigLoader.getInstance().getCoreThreadNum();
        maxThreadNum = ConfigLoader.getInstance().getMaxThreadNum();
        queueNum = ConfigLoader.getInstance().getQueueNum();
        writerIdleTime = ConfigLoader.getInstance().getWriterIdleTime();
        readerIdleTime = ConfigLoader.getInstance().getReaderIdleTime();
        allIdleTime = ConfigLoader.getInstance().getAllIdleTime();
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
}
