package com.zhang.netty.common;

public enum NettyResponseStatus {
    SUCCESS("ok"),
    CLIENT_SEND_ERROR("client_send_error"),
    TIME_OUT("time_out"),
    EXCEPTION("exception");

    private final String message;

    NettyResponseStatus(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }
}
