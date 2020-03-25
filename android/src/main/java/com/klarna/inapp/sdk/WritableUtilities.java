package com.klarna.inapp.sdk;

import com.facebook.react.bridge.Arguments;
import com.facebook.react.bridge.WritableArray;
import com.facebook.react.bridge.WritableMap;

import java.util.List;
import java.util.Map;

class WritableUtilities {

    static WritableArray createWritableArray(List<String> stringList) {
        WritableArray array = Arguments.createArray();
        if (stringList != null) {
            for (String listItem : stringList) {
                if (listItem == null) {
                    array.pushNull();
                } else {
                    array.pushString(listItem);
                }
            }
        }
        return array;
    }

    static WritableMap createWritableMap(Map<String, String> stringMap) {
        WritableMap map = Arguments.createMap();
        if (stringMap != null) {
            for (Map.Entry<String, String> entry : stringMap.entrySet()) {
                if (entry.getValue() == null) {
                    map.putNull(entry.getKey());
                } else {
                    map.putString(entry.getKey(), entry.getValue());
                }
            }
        }
        return map;
    }
}
