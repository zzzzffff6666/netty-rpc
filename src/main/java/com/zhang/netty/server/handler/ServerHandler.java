package com.zhang.netty.server.handler;

import com.zhang.netty.common.AttributeFunction;
import com.zhang.netty.common.NettyProtocol;
import com.zhang.netty.enums.NettyApiType;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import lombok.extern.slf4j.Slf4j;

import java.nio.charset.StandardCharsets;

@Slf4j
public class ServerHandler extends ChannelInboundHandlerAdapter {

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        log.info("Channel is active, {}", ctx.channel());
        //ctx.writeAndFlush(getProtocol("I'm server!"));
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) {
        NettyProtocol protocol = (NettyProtocol) msg;
        log.info("Server received: {}", protocol.toString());
        log.info("content: [{}]", new String(protocol.getData(), StandardCharsets.UTF_8));
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        cause.printStackTrace();
        ctx.close();
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
        return new NettyProtocol(NettyApiType.REQUEST.getType(), (short) 0, AttributeFunction.CHECK_SUM, data.getBytes(StandardCharsets.UTF_8));
    }
}
