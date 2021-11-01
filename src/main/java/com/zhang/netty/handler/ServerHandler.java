package com.zhang.netty.handler;

import com.zhang.netty.protocol.AttributeFunction;
import com.zhang.netty.protocol.NettyProtocol;
import com.zhang.netty.enums.NettyApiType;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import lombok.extern.slf4j.Slf4j;

import java.nio.charset.StandardCharsets;

@Slf4j
@ChannelHandler.Sharable
public class ServerHandler extends ChannelInboundHandlerAdapter {

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        log.info("Channel is active, {}", ctx.channel());
        ctx.writeAndFlush(getProtocol("I'm server!"));
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) {
        NettyProtocol protocol = (NettyProtocol) msg;
        log.info("Server received: {}", protocol.toString());
        log.info("content: [{}]", new String(protocol.getData(), StandardCharsets.UTF_8));
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
