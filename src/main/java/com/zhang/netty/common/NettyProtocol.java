package com.zhang.netty.common;

public class NettyProtocol {
    public static final byte MAGIC_V1 = 1;

    // 协议头信息
    public static final int MAGIC_LENGTH = 1;
    public static final int API_TYPE_LENGTH = 1;
    public static final int API_KEY_LENGTH = 2;
    public static final int ATTRIBUTE_LENGTH = 1;
    public static final int CHECK_SUM_LENGTH = 4;

    // 版本
    protected byte magic;
    // 请求类型
    protected byte apiType;
    // 请求Key
    protected short apiKey;
    // 控制位
    protected byte attribute;
    // 请求内容
    protected byte[] data;

    public NettyProtocol(byte apiType, short apiKey, byte[] data) {
        this(MAGIC_V1, apiType, apiKey, AttributeFunction.NO_FUNCTION, data);
    }

    public NettyProtocol(byte apiType, short apiKey, byte attribute, byte[] data) {
        this(MAGIC_V1, apiType, apiKey, attribute, data);
    }

    public NettyProtocol(byte magic, byte apiType, short apiKey, byte[] data) {
        this(magic, apiType, apiKey, AttributeFunction.NO_FUNCTION, data);
    }

    public NettyProtocol(byte magic, byte apiType, short apiKey, byte attribute, byte[] data) {
        this.magic = magic;
        this.apiType = apiType;
        this.apiKey = apiKey;
        this.attribute = attribute;
        this.data = data;
    }

    public static int getBasicHeaderLength() {
        return MAGIC_LENGTH + API_TYPE_LENGTH + API_KEY_LENGTH + ATTRIBUTE_LENGTH;
    }

    public byte getMagic() {
        return magic;
    }

    public byte getApiType() {
        return apiType;
    }

    public short getApiKey() {
        return apiKey;
    }

    public byte getAttribute() {
        return attribute;
    }

    public byte[] getData() {
        return data;
    }
}
