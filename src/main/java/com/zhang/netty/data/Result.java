package com.zhang.netty.data;

import java.util.HashMap;
import java.util.Map;

public class Result {
    private static final String CODE_TAG = "code";
    private static final String MESSAGE_TAG = "msg";
    private static final String DATA_TAG = "data";

    private static final String SUCCESS = "success";
    private static final String ERROR = "error";

    private static final int SUCCESS_CODE = 0;
    private static final int ERROR_CODE = 1;

    public static Map<String, Object> success() {
        return success(null);
    }

    public static Map<String, Object> success(Object data) {
        Map<String, Object> result = new HashMap<>();
        result.put(CODE_TAG, SUCCESS_CODE);
        result.put(MESSAGE_TAG, SUCCESS);
        result.put(DATA_TAG, data);
        return result;
    }

    public static Map<String, Object> error(String msg) {
        Map<String, Object> result = new HashMap<>();
        result.put(CODE_TAG, ERROR_CODE);
        result.put(MESSAGE_TAG, ERROR);
        result.put(DATA_TAG, msg);
        return result;
    }
}
