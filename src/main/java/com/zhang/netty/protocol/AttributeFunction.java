package com.zhang.netty.protocol;

public class AttributeFunction {
    public static final byte NO_FUNCTION = (byte)0x00;
    public static final byte CHECK_SUM = (byte)0x01;

    public static boolean isCheckSum(byte attribute) {
        return (attribute & CHECK_SUM) == CHECK_SUM;
    }
}
