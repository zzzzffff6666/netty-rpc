package com.zhang.netty.process.model;

import com.zhang.netty.protocol.NettyProtocol;
import io.netty.channel.ChannelHandlerContext;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class EventContext {
    private final ChannelHandlerContext channelHandlerContext;

    public EventContext(ChannelHandlerContext ctx) {
        channelHandlerContext = ctx;
    }

    public ChannelHandlerContext getChannelHandlerContext() {
        return channelHandlerContext;
    }

    public void writeAndFlush(NettyProtocol response) {
        channelHandlerContext.writeAndFlush(response).addListener(future -> {
            if (!future.isSuccess()) {
                log.error("ServerHandler send failed, apiKey={}", response.getApiKey());
            }
        });
    }
}
