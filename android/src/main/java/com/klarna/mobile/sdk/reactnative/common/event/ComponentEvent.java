package com.klarna.mobile.sdk.reactnative.common.event;

import androidx.annotation.IdRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.facebook.react.bridge.Arguments;
import com.facebook.react.bridge.WritableMap;
import com.facebook.react.uimanager.events.Event;

public class ComponentEvent extends Event<ComponentEvent> {
    @NonNull
    private final String eventName;

    @Nullable
    private final WritableMap additionalParams;

    public ComponentEvent(@IdRes int viewId, @NonNull String eventName, @Nullable WritableMap additionalParams) {
        super(viewId);
        this.eventName = eventName;
        this.additionalParams = additionalParams;
    }

    @Override
    public String getEventName() {
        return eventName;
    }

    @Nullable
    @Override
    protected WritableMap getEventData() {
        if (additionalParams != null) {
            return additionalParams;
        }
        return Arguments.createMap();
    }
}
