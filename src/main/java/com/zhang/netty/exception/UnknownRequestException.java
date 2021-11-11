package com.zhang.netty.exception;

public class UnknownRequestException extends HandlerException {
    public UnknownRequestException(String msg) {
        super(msg);
    }
}
