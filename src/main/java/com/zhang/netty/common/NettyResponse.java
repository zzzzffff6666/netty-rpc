package com.zhang.netty.common;

public class NettyResponse extends NettyProtocol {
    private NettyResponseStatus nettyResponseStatus = NettyResponseStatus.SUCCESS;
    private String errorMsg;

    public NettyResponse(byte magic, short apiKey, byte attribute, byte[] data) {
        super(magic, NettyProtocolType.RESPONSE, apiKey, attribute, data);
    }

    public NettyResponse(byte magic, short apiKey, byte[] data) {
        super(magic, NettyProtocolType.RESPONSE, apiKey, data);
    }

    public NettyResponse(short apiKey, byte[] data) {
        super(NettyProtocolType.RESPONSE, apiKey, data);
    }

    public NettyResponseStatus getNettyResponseStatus() {
        return nettyResponseStatus;
    }

    public void setNettyResponseStatus(NettyResponseStatus nettyResponseStatus) {
        this.nettyResponseStatus = nettyResponseStatus;
    }

    public String getErrorMsg() {
        return errorMsg;
    }

    public void setErrorMsg(String errorMsg) {
        this.errorMsg = errorMsg;
    }
}
