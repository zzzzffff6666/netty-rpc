package com.zhang.netty.handler;

import com.zhang.netty.exception.DirtyStreamException;
import com.zhang.netty.protocol.AttributeFunction;
import com.zhang.netty.protocol.NettyProtocol;
import com.zhang.netty.util.NettyUtil;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageDecoder;

import java.util.List;

@ChannelHandler.Sharable //可被安全共享
public class NettyDecoder extends MessageToMessageDecoder<ByteBuf> {
    // 请求/响应头长度
    private static final int HEADER_SIZE = 5;

    // 请求/响应体长度
    private static final int BODY_SIZE = 10485760; //10MB

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> list) throws Exception {
        if (in.readableBytes() < HEADER_SIZE) {
            throw new DirtyStreamException(String.format("Dirty input stream, readableBytes[%d] < HEADER_SIZE[%d]", in.readableBytes(), HEADER_SIZE));
        }
        int totalLength = in.readInt();
        int startIndex = in.readerIndex();
        byte magic = in.readByte();
        byte apiType = in.readByte();
        short apiKey = in. readShort();
        byte attribute = in.readByte();
        boolean checkSum = AttributeFunction.isCheckSum(attribute);
        int dataLength = totalLength - NettyProtocol.getBasicHeaderLength();
        if (checkSum) {
            dataLength -= NettyProtocol.CHECK_SUM_LENGTH;
        }
        if (dataLength <= 0 || dataLength > BODY_SIZE) {
            throw new DirtyStreamException(String.format("Dirty input stream, dataLength[%d] doesn't between (0, MAX_BODY_SIZE]", dataLength));
        }
        byte[] data = new byte[dataLength];
        in.readBytes(data);
        if (checkSum) {
            checkSum(in, startIndex);
        }
        addProtocol(magic, apiType, apiKey, attribute, data, list);
    }

    private void checkSum(ByteBuf in, int startIndex) throws Exception {
        int endIndex = in.readerIndex();
        byte[] expectedCheckSum = new byte[NettyProtocol.CHECK_SUM_LENGTH];
        in.readBytes(expectedCheckSum);
        byte[] frame = new byte[endIndex - startIndex];
        in.getBytes(startIndex, frame, 0, endIndex - startIndex);
        byte[] actualCheckSum = NettyUtil.getCheckSum(frame);
        if (!NettyUtil.bytesEquals(expectedCheckSum, actualCheckSum)) {
            throw new DirtyStreamException("Dirty input stream, check sum error");
        }
    }

    private void addProtocol(byte magic, byte apiType, short apiKey, byte attribute, byte[] data, List<Object> list) {
        list.add(new NettyProtocol(magic, apiType, apiKey, attribute, data));
    }
}
