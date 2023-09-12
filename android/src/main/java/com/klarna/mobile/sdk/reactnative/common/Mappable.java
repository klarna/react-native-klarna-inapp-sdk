package com.klarna.mobile.sdk.reactnative.common;

import androidx.annotation.NonNull;

import com.facebook.react.bridge.ReadableMap;

import java.util.Map;

public interface Mappable {
    @NonNull
    ReadableMap buildMap();
}
