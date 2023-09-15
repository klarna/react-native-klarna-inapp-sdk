package com.klarna.mobile.sdk.reactnative.common;

import com.facebook.react.bridge.Arguments;
import com.facebook.react.bridge.ReadableArray;
import com.facebook.react.bridge.ReadableMap;
import com.facebook.react.bridge.WritableArray;
import com.facebook.react.bridge.WritableMap;

import java.util.List;
import java.util.Map;

public class ArgumentsUtil {

    public static WritableMap createMap(Map<String, Object> sourceMap) {
        WritableMap map = Arguments.createMap();
        for (Map.Entry<String, Object> entry : sourceMap.entrySet()) {
            String key = entry.getKey();
            Object value = entry.getValue();
            if (value == null) {
                map.putNull(key);
            } else if (value instanceof Boolean) {
                map.putBoolean(key, (Boolean) value);
            } else if (value instanceof String) {
                map.putString(key, (String) value);
            } else if (value instanceof Integer) {
                map.putInt(key, (Integer) value);
            } else if (value instanceof Double) {
                map.putDouble(key, (Double) value);
            } else if (value instanceof ReadableArray) {
                map.putArray(key, (ReadableArray) value);
            } else if (value instanceof ReadableMap) {
                map.putMap(key, (ReadableMap) value);
            }
        }
        return map;
    }

    public static WritableArray createArray(List<?> sourceList) {
        WritableArray array = Arguments.createArray();
        if (sourceList != null && sourceList.size() > 0) {
            for (int i = 0; i < sourceList.size(); i++) {
                Object value = sourceList.get(i);
                if (value == null) {
                    array.pushNull();
                } else if (value instanceof Boolean) {
                    array.pushBoolean((Boolean) value);
                } else if (value instanceof String) {
                    array.pushString((String) value);
                } else if (value instanceof Integer) {
                    array.pushInt((Integer) value);
                } else if (value instanceof Double) {
                    array.pushDouble((Double) value);
                } else if (value instanceof ReadableArray) {
                    array.pushArray((ReadableArray) value);
                } else if (value instanceof ReadableMap) {
                    array.pushMap((ReadableMap) value);
                }
            }
        }
        return array;
    }
}
