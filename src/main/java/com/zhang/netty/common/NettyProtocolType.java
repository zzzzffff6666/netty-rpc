package com.zhang.netty.common;

public interface NettyProtocolType {
    byte REQUEST = (byte) 0x00;
    byte RESPONSE = (byte) 0x01;
    byte EXCEPTION = (byte) 0x02;
}
