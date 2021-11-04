package com.zhang.netty.protocol.enums;

public enum NettyApiKey {
    INSERT((short) 1, "添加资源"),
    DELETE((short) 2, "删除数据"),
    UPDATE((short) 3, "修改数据"),
    SELECT((short) 4, "查询数据"),
    RESPONSE((short) 5, "请求响应"),
    OTHERS((short) 5, "其他操作");

    final short key;
    final String name;

    NettyApiKey(short key, String name) {
        this.key = key;
        this.name = name;
    }

    public short getKey() {
        return key;
    }

    public String getName() {
        return name;
    }
}
