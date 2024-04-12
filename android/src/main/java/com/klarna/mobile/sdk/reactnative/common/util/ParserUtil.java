package com.klarna.mobile.sdk.reactnative.common.util;

import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class ParserUtil {
    public static final Gson gson = new GsonBuilder().create();

    public static String toJson(Object obj) {
        return gson.toJson(obj);
    }

    public static String toJsonSafe(Object obj) {
        try {
            return gson.toJson(obj);
        } catch (Throwable t) {
            Log.e(ParserUtil.class.getSimpleName(), "Failed to serialize object to string with Gson: " + t.getMessage());
            return null;
        }
    }

    public static <T> T fromJson(String json, Class<T> cls) {
        return gson.fromJson(json, cls);
    }

    public static <T> T fromJsonSafe(String json, Class<T> cls) {
        try {
            return gson.fromJson(json, cls);
        } catch (Throwable t) {
            Log.e(ParserUtil.class.getSimpleName(), "Failed to deserialize object from string with Gson: " + t.getMessage());
            return null;
        }
    }
}
