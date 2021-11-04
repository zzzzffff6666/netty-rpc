package com.zhang.netty.handler;

import com.alibaba.fastjson.JSON;
import com.zhang.netty.data.Result;
import com.zhang.netty.protocol.enums.NettyApiKey;
import com.zhang.netty.protocol.enums.NettyApiType;
import com.zhang.netty.protocol.AttributeFunction;
import com.zhang.netty.protocol.NettyProtocol;
import io.netty.channel.ChannelDuplexHandler;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import lombok.extern.slf4j.Slf4j;

import java.nio.charset.StandardCharsets;

@Slf4j
@ChannelHandler.Sharable
public class ExceptionHandler extends ChannelDuplexHandler {
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        if (cause instanceof Exception) {
            log.warn("Exception caught: {}", cause.toString());
            String result = JSON.toJSONString(Result.error(cause.getMessage()));
            NettyProtocol protocol = NettyProtocol.builder()
                    .apiType(NettyApiType.EXCEPTION.getType())
                    .apiKey(NettyApiKey.RESPONSE.getKey())
                    .attribute(AttributeFunction.CHECK_SUM)
                    .data(result.getBytes(StandardCharsets.UTF_8))
                    .build();
            ctx.writeAndFlush(protocol);
        } else if (cause instanceof Error) {
            log.error("Error caught: {}", cause.toString());
            String result = JSON.toJSONString(Result.error(cause.getMessage()));
            NettyProtocol protocol = NettyProtocol.builder()
                    .apiType(NettyApiType.ERROR.getType())
                    .apiKey(NettyApiKey.RESPONSE.getKey())
                    .attribute(AttributeFunction.CHECK_SUM)
                    .data(result.getBytes(StandardCharsets.UTF_8))
                    .build();
            ctx.writeAndFlush(protocol);
            ctx.close();
        }
    }
}
