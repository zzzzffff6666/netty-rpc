package com.zhang.netty.common;

import com.zhang.netty.enums.NettyApiType;

public class NettyRequest extends NettyProtocol {

    public NettyRequest(byte magic, short apiKey, byte attribute, byte[] data) {
        super(magic, NettyApiType.REQUEST.getType(), apiKey, attribute, data);
    }

    public NettyRequest(byte magic, short apiKey, byte[] data) {
        super(magic, NettyApiType.REQUEST.getType(), apiKey, data);
    }

    public NettyRequest(short apiKey, byte[] data) {
        super(NettyApiType.REQUEST.getType(), apiKey, data);
    }
}
