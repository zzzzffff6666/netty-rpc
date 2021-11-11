package com.zhang.netty.handler;

import com.zhang.netty.process.ProcessorFactory;
import com.zhang.netty.process.model.EventContext;
import com.zhang.netty.protocol.AttributeFunction;
import com.zhang.netty.protocol.NettyProtocol;
import com.zhang.netty.protocol.NettyApiType;
import com.zhang.netty.util.NettyUtil;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import lombok.extern.slf4j.Slf4j;

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
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) {
        NettyProtocol protocol = (NettyProtocol) msg;
        log.info("content: [{}]", NettyUtil.bytes2data(protocol.getData()));
        EventContext eventContext = new EventContext(ctx);
        if (protocol.getApiType() == NettyApiType.REQUEST) {
            processGroup.execute(() -> ProcessorFactory.processEvent(protocol, eventContext));
        } else if (protocol.getApiType() == NettyApiType.RESPONSE) {

        } else if (protocol.getApiType() == NettyApiType.EXCEPTION) {

        } else {

        }
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
                .apiType(NettyApiType.REQUEST)
                .apiKey((short) 0)
                .attribute(AttributeFunction.CHECK_SUM)
                .data(NettyUtil.data2bytes(data))
                .build();
    }
}
