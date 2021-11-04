package com.zhang.netty.handler;

import com.zhang.netty.protocol.AttributeFunction;
import com.zhang.netty.protocol.NettyProtocol;
import com.zhang.netty.protocol.enums.NettyApiType;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import lombok.extern.slf4j.Slf4j;

import java.nio.charset.StandardCharsets;
import java.util.concurrent.ExecutorService;

@Slf4j
public class ClientHandler extends ChannelInboundHandlerAdapter {
    private ExecutorService processGroup;

    public ClientHandler(ExecutorService processGroup) {
        this.processGroup = processGroup;
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) {
        log.info("Channel is active, {}", ctx.channel());
        ctx.writeAndFlush(getProtocol("I'm client!"));
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        NettyProtocol protocol = (NettyProtocol) msg;
        log.info("Client received: {}", protocol.toString());
        log.info("content: [{}]", new String(protocol.getData(), StandardCharsets.UTF_8));
        //throw new Exception("test");
    }

    @Override
    public void channelUnregistered(ChannelHandlerContext ctx) throws Exception {
        log.info("channel unregistered");
        ctx.close();
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        log.info("channel inactive");
        ctx.close();
    }

    public NettyProtocol getProtocol(String data) {
        return NettyProtocol.builder()
                .apiType(NettyApiType.REQUEST.getType())
                .apiKey((short) 0)
                .attribute(AttributeFunction.CHECK_SUM)
                .data(data.getBytes(StandardCharsets.UTF_8))
                .build();
    }
}
