package com.klarna.mobile.sdk.reactnative.standalonewebview;

import androidx.annotation.IdRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.facebook.react.bridge.Arguments;
import com.facebook.react.bridge.WritableMap;
import com.facebook.react.uimanager.events.Event;

/**
 * A `KlarnaStandaloneWebViewEvent` builds the event that will be eventually sent via `on<event>`.
 * <p>
 * It consists of a flat JSON object with a single `name` parameter for the event name as well as
 * other, optional parameters depending on the function that was called.
 */
public class KlarnaStandaloneWebViewEvent extends Event<KlarnaStandaloneWebViewEvent> {

    // This event is sent just before loading a URL
    public static final String EVENT_NAME_ON_BEFORE_LOAD = "onBeforeLoad";
    // This event is sent when loading a URL is done
    public static final String EVENT_NAME_ON_LOAD = "onLoad";

    @NonNull
    private final String eventName;

    @Nullable
    private final WritableMap additionalParams;

    public KlarnaStandaloneWebViewEvent(@IdRes int viewId, @NonNull String eventName, @Nullable WritableMap additionalParams) {
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
