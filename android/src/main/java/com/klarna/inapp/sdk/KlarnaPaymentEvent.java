package com.klarna.inapp.sdk;

import androidx.annotation.IdRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.facebook.react.bridge.Arguments;
import com.facebook.react.bridge.WritableMap;
import com.facebook.react.uimanager.events.Event;
import com.facebook.react.uimanager.events.RCTEventEmitter;

import java.util.HashMap;
import java.util.Map;

/**
 * A `KlarnaPaymentEvent` builds the event that will be eventually sent via `on<event>`.
 *
 * It consists of a flat JSON object with a single `name` parameter for the event name as well as
 * other, optional parameters depending on the function that was called.
 */
public class KlarnaPaymentEvent extends Event<KlarnaPaymentEvent> {

    public static final String EVENT_NAME_ON_INITIALIZE = "onInitialized";
    public static final String EVENT_NAME_ON_LOAD = "onLoaded";
    public static final String EVENT_NAME_ON_LOAD_PAYMENT_REVIEW = "onLoadedPaymentReview";
    public static final String EVENT_NAME_ON_AUTHORIZE = "onAuthorized";
    public static final String EVENT_NAME_ON_REAUTHORIZE = "onReauthorized";
    public static final String EVENT_NAME_ON_FINALIZE = "onFinalized";
    public static final String EVENT_NAME_ON_ERROR = "onError";

    @NonNull
    private final String eventName;

    @Nullable
    private final Map<String, Object> additionalParams;

    public KlarnaPaymentEvent(@IdRes int viewId, @NonNull String eventName, @Nullable Map<String, Object> additionalParams) {
        super(viewId);
        this.eventName = eventName;
        this.additionalParams = additionalParams;
    }

    @Override
    public String getEventName() {
        return eventName;
    }

    /**
     * Composes and sends the event JSON object being sent up to JS.
     * @param rctEventEmitter
     */
    @Override
    public void dispatch(RCTEventEmitter rctEventEmitter) {
        Map<String, Object> map = new HashMap<>();

        if (additionalParams != null) {
            map.putAll(additionalParams);
        }

        WritableMap eventData = Arguments.makeNativeMap(map);
        rctEventEmitter.receiveEvent(getViewTag(), getEventName(), eventData);
    }
}
