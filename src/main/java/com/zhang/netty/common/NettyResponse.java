package com.zhang.netty.common;

import com.zhang.netty.enums.NettyApiType;
import com.zhang.netty.enums.ResponseStatus;

public class NettyResponse extends NettyProtocol {
    private ResponseStatus responseStatus = ResponseStatus.SUCCESS;
    private String errorMsg;

    public NettyResponse(byte magic, short apiKey, byte attribute, byte[] data) {
        super(magic, NettyApiType.RESPONSE.getType(), apiKey, attribute, data);
    }

    public NettyResponse(byte magic, short apiKey, byte[] data) {
        super(magic, NettyApiType.RESPONSE.getType(), apiKey, data);
    }

    public NettyResponse(short apiKey, byte[] data) {
        super(NettyApiType.RESPONSE.getType(), apiKey, data);
    }

    public ResponseStatus getResponseStatus() {
        return responseStatus;
    }

    public void setResponseStatus(ResponseStatus responseStatus) {
        this.responseStatus = responseStatus;
    }

    public String getErrorMsg() {
        return errorMsg;
    }

    public void setErrorMsg(String errorMsg) {
        this.errorMsg = errorMsg;
    }
}
