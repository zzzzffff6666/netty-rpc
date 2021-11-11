package com.zhang.netty.handler;

import com.zhang.netty.process.ProcessorFactory;
import com.zhang.netty.process.model.EventContext;
import com.zhang.netty.protocol.AttributeFunction;
import com.zhang.netty.protocol.NettyProtocol;
import com.zhang.netty.enums.NettyApiType;
import com.zhang.netty.util.NettyUtil;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import lombok.extern.slf4j.Slf4j;

import java.nio.charset.StandardCharsets;
import java.util.concurrent.ExecutorService;

@Slf4j
@ChannelHandler.Sharable
public class ServerHandler extends ChannelInboundHandlerAdapter {
    private ExecutorService processGroup;

    public ServerHandler(ExecutorService processGroup) {
        this.processGroup = processGroup;
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        log.info("Channel is active, name = {}", ctx.name());
        ctx.writeAndFlush(getProtocol("I'm server!"));
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) {
        NettyProtocol protocol = (NettyProtocol) msg;
        log.info("content: [{}]", new String(protocol.getData(), StandardCharsets.UTF_8));
        EventContext eventContext = new EventContext(ctx);
        ProcessorFactory.processEvent(protocol, eventContext);
    }

    @Override
    public void channelUnregistered(ChannelHandlerContext ctx) throws Exception {
        log.info("channel unregistered, name = {}", ctx.name());
        ctx.close();
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        log.info("channel inactive, name = {}", ctx.name());
        ctx.close();
    }

    public NettyProtocol getProtocol(String data) {
        return NettyProtocol.builder()
                .apiType(NettyApiType.REQUEST.getType())
                .apiKey((short) 0)
                .attribute(AttributeFunction.CHECK_SUM)
                .data(NettyUtil.data2bytes(data))
                .build();
    }
}
