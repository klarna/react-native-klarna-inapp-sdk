package com.klarna.mobile.sdk.reactnative;

import androidx.annotation.NonNull;

import java.util.Map;

interface Mappable {
    @NonNull
    Map<String, Object> buildMap();
}
