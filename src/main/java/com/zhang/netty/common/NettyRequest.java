package com.zhang.netty.common;

public class NettyRequest extends NettyProtocol {

    public NettyRequest(byte magic, short apiKey, byte attribute, byte[] data) {
        super(magic, NettyProtocolType.REQUEST, apiKey, attribute, data);
    }

    public NettyRequest(byte magic, short apiKey, byte[] data) {
        super(magic, NettyProtocolType.REQUEST, apiKey, data);
    }

    public NettyRequest(short apiKey, byte[] data) {
        super(NettyProtocolType.REQUEST, apiKey, data);
    }
}
