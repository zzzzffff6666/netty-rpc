package com.zhang.netty.client.handler;

import com.zhang.netty.common.AttributeFunction;
import com.zhang.netty.common.NettyProtocol;
import com.zhang.netty.common.NettyProtocolType;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

import java.nio.charset.StandardCharsets;

public class ClientHandler extends ChannelInboundHandlerAdapter {

    @Override
    public void channelActive(ChannelHandlerContext chc) {
        System.out.println("Channel is active, " + chc.channel());
        chc.writeAndFlush(getProtocol("Netty rocks!"));
    }

    @Override
    public void channelRead(ChannelHandlerContext chc, Object msg) throws Exception {
        NettyProtocol protocol = (NettyProtocol) msg;
        System.out.println("Client received: " + protocol.toString());
        System.out.println("data is " + new String(protocol.getData(), StandardCharsets.UTF_8));
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
