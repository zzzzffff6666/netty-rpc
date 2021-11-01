package com.zhang.netty.handler;

import com.zhang.netty.protocol.AttributeFunction;
import com.zhang.netty.protocol.NettyProtocol;
import com.zhang.netty.util.NettyUtil;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

@ChannelHandler.Sharable //可被安全共享
public class NettyEncoder extends MessageToByteEncoder<NettyProtocol> {

    @Override
    protected void encode(ChannelHandlerContext ctx, NettyProtocol msg, ByteBuf out) {
        int index = out.writerIndex();
        out.writeByte(msg.getMagic());
        out.writeByte(msg.getApiType());
        out.writeShort(msg.getApiKey());
        out.writeByte(msg.getAttribute());
        out.writeBytes(msg.getData());
        if (AttributeFunction.isCheckSum(msg.getAttribute())) {
            byte[] frame = new byte[out.readableBytes()];
            out.getBytes(index, frame);
            out.writeBytes(NettyUtil.getCheckSum(frame));
        }
    }
}
