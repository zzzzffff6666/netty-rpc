package com.zhang.netty.client;

import com.zhang.netty.config.ConfigLoader;
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
    private final String server;
    private final int port;

    private final int coreThreadNum;
    private final int maxThreadNum;
    private final int queueNum;
    private final String threadName;

    private final int writerIdleTime;
    private final int readerIdleTime;
    private final int allIdleTime;

    public NettyClient() {
        server = ConfigLoader.getInstance().getServer();
        port = ConfigLoader.getInstance().getPort();
        threadName = ConfigLoader.getInstance().getThreadName();
        coreThreadNum = ConfigLoader.getInstance().getCoreThreadNum();
        maxThreadNum = ConfigLoader.getInstance().getMaxThreadNum();
        queueNum = ConfigLoader.getInstance().getQueueNum();
        writerIdleTime = ConfigLoader.getInstance().getWriterIdleTime();
        readerIdleTime = ConfigLoader.getInstance().getReaderIdleTime();
        allIdleTime = ConfigLoader.getInstance().getAllIdleTime();
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
                    .remoteAddress(new InetSocketAddress(server, port))
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
}
