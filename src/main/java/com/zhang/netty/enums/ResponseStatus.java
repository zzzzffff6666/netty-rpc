package com.zhang.netty.enums;

public enum ResponseStatus {
    SUCCESS("ok"),
    CLIENT_SEND_ERROR("client_send_error"),
    TIME_OUT("time_out"),
    EXCEPTION("exception");

    private final String message;

    ResponseStatus(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }
}