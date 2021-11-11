package com.zhang.netty.protocol;

public class NettyApiType {
    public static final byte REQUEST = (byte) 0x00;
    public static final byte RESPONSE = (byte) 0x01;
    public static final byte EXCEPTION = (byte) 0x02;
    public static final byte ERROR= (byte) 0x03;
}
