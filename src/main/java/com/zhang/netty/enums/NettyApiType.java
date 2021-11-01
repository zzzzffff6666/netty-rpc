package com.zhang.netty.enums;

public enum NettyApiType {
    REQUEST((byte) 0x00),
    RESPONSE((byte) 0x01),
    EXCEPTION((byte) 0x02),
    ERROR((byte) 0x03);

    final byte type;

    NettyApiType(byte type) {
        this.type = type;
    }

    public byte getType() {
        return type;
    }
}
