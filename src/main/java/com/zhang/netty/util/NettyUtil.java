package com.zhang.netty.util;

import java.util.zip.Adler32;

public class NettyUtil {

    public static byte[] getCheckSum(byte[] bytes) {
        Adler32 a32 = new Adler32();
        a32.update(bytes, 0, bytes.length);
        int sum = (int)a32.getValue();
        return new byte[]{(byte)(sum >> 24), (byte)(sum >> 16), (byte)(sum >> 8), (byte)sum};
    }

    public static boolean bytesEquals(byte[] bytes1, byte[] bytes2) {
        boolean equals = true;
        if (bytes1 != null || bytes2 != null) {
            if (bytes1 != null && bytes2 != null) {
                if (bytes1.length != bytes2.length) {
                    equals = false;
                } else {
                    int len = bytes1.length;
                    for (int i = 0; i < len; ++i) {
                        if (bytes1[i] != bytes2[i]) {
                            equals = false;
                            break;
                        }
                    }
                }
            } else {
                equals = false;
            }
        }
        return equals;
    }
}
