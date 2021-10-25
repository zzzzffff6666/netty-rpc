package com.zhang.netty.server.handler;

import com.zhang.netty.common.AttributeFunction;
import com.zhang.netty.common.NettyProtocol;
import com.zhang.netty.common.NettyProtocolType;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

import java.nio.charset.StandardCharsets;

public class ServerHandler extends ChannelInboundHandlerAdapter {
    @Override
    public void channelRead(ChannelHandlerContext chc, Object msg) {
        NettyProtocol protocol = (NettyProtocol) msg;
        System.out.println("Server received: " + protocol.toString());
        System.out.println("data is " + new String(protocol.getData(), StandardCharsets.UTF_8));
    }

    @Override
    public void channelReadComplete(ChannelHandlerContext chc) throws Exception {
        //chc.writeAndFlush(Unpooled.EMPTY_BUFFER).addListener(ChannelFutureListener.CLOSE);
        chc.writeAndFlush(getProtocol("I receive"));
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext chc, Throwable cause) {
        cause.printStackTrace();
        chc.close();
    }

    public NettyProtocol getProtocol(String data) {
        return new NettyProtocol(NettyProtocolType.REQUEST, (short) 0, AttributeFunction.CHECK_SUM, data.getBytes(StandardCharsets.UTF_8));
    }
}
