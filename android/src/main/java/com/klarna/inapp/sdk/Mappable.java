package com.klarna.inapp.sdk;

import androidx.annotation.NonNull;

import java.util.Map;

interface Mappable {
    @NonNull
    Map<String, Object> buildMap();
}
