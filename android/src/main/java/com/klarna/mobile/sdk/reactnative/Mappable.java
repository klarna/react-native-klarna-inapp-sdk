package com.klarna.mobile.sdk.reactnative;

import androidx.annotation.NonNull;

import com.facebook.react.bridge.ReadableMap;

import java.util.Map;

interface Mappable {
    @NonNull
    ReadableMap buildMap();
}
