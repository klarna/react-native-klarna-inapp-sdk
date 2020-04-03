package com.klarna.inapp.sdk;

import com.facebook.react.bridge.WritableMap;

import javax.annotation.Nonnull;

public interface WritableObject {
    @Nonnull
    WritableMap toWritableMap();
}
