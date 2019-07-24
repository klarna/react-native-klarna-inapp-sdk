package com.klarna;

import android.support.annotation.IdRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.facebook.react.bridge.Arguments;
import com.facebook.react.bridge.WritableMap;
import com.facebook.react.uimanager.events.Event;
import com.facebook.react.uimanager.events.RCTEventEmitter;

import java.util.HashMap;
import java.util.Map;

/**
 * A `KlarnaPaymentEvent` is the event type being sent via `onEvent`.
 *
 * It consists of a flat JSON object with a single `name` parameter for the event name as well as
 * other, optional parameters depending on the function that was called.
 */
public class KlarnaPaymentEvent extends Event<KlarnaPaymentEvent> {

    public static final String EVENT_NAME_ON_CHANGE = "onEvent";

    @NonNull
    private final String name;

    @Nullable
    private final Map<String, Object> additionalParams;

    public KlarnaPaymentEvent(@IdRes int viewId, @NonNull String name, Map<String, Object> additionalParams) {
        super(viewId);
        this.name = name;
        this.additionalParams = additionalParams;
    }

    @Override
    public String getEventName() {
        return EVENT_NAME_ON_CHANGE;
    }

    /**
     * Composes and sends the event JSON object being sent up to JS.
     * @param rctEventEmitter
     */
    @Override
    public void dispatch(RCTEventEmitter rctEventEmitter) {
        Map<String, Object> map = new HashMap<>();
        map.put("name", name);

        if (additionalParams != null) {
            map.putAll(additionalParams);
        }

        WritableMap eventData = Arguments.makeNativeMap(map);
        rctEventEmitter.receiveEvent(getViewTag(), getEventName(), eventData);
    }
}
