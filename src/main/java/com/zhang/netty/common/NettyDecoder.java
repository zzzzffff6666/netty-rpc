package com.zhang.netty.common;

import com.zhang.netty.exception.DirtyStreamException;
import com.zhang.netty.util.NettyUtil;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageDecoder;

import java.util.List;

@ChannelHandler.Sharable //可被安全共享
public class NettyDecoder extends MessageToMessageDecoder<ByteBuf> {
    // 请求/响应头长度
    private static final int HEADER_SIZE = 6;

    // 请求/响应体长度
    private static final int BODY_SIZE = 10 * 1024 * 1024;

    @Override
    protected void decode(ChannelHandlerContext chc, ByteBuf in, List<Object> list) throws Exception {
        if (in.readableBytes() < HEADER_SIZE) {
            throw new DirtyStreamException(String.format("Dirty input stream, readableBytes[%d] < HEADER_SIZE[%d]", in.readableBytes(), HEADER_SIZE));
        }
        int startIndex = in.readerIndex();
        int totalLength = in.readableBytes();
        byte magic = in.readByte();
        byte apiType = in.readByte();
        short apiKey = in. readShort();
        byte attribute = in.readByte();
        boolean checkSum = AttributeFunction.isCheckSum(attribute);
        int dataLength = totalLength - NettyProtocol.getBasicHeaderLength();
        if (checkSum) {
            dataLength -= NettyProtocol.CHECK_SUM_LENGTH;
        }
        if (dataLength <= 0) {
            throw new DirtyStreamException(String.format("Dirty input stream, dataLength[%d] <= 0", dataLength));
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
            throw new DirtyStreamException("Dirty input stream, check sun error");
        }
    }

    private void addProtocol(byte magic, byte apiType, short apiKey, byte attribute, byte[] data, List<Object> list) {
        if (apiType == NettyProtocolType.RESPONSE) {
            list.add(new NettyResponse(magic, apiKey, attribute, data));
        } else if (apiType == NettyProtocolType.REQUEST) {
            list.add(new NettyRequest(magic, apiKey, attribute, data));
        } else if (apiType == NettyProtocolType.EXCEPTION) {
            list.add(new NettyResponse(magic, apiKey, attribute, data));
        }
    }
}
