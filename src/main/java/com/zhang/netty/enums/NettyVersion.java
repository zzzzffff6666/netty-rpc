package com.zhang.netty.enums;

public enum NettyVersion {
    TIAN((byte) 1, "天"),
    DI((byte) 2, "地"),
    XUAN((byte) 3, "玄"),
    HUANG((byte) 4, "黄");
    final byte version;
    final String name;
    NettyVersion(byte version, String name) {
        this.version = version;
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public byte getVersion() {
        return version;
    }
}
