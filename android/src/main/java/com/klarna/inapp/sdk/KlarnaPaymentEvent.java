package com.klarna.inapp.sdk;

import androidx.annotation.IdRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.facebook.react.bridge.Arguments;
import com.facebook.react.bridge.WritableMap;
import com.facebook.react.uimanager.events.Event;

/**
 * A `KlarnaPaymentEvent` builds the event that will be eventually sent via `on<event>`.
 *
 * It consists of a flat JSON object with a single `name` parameter for the event name as well as
 * other, optional parameters depending on the function that was called.
 */
public class KlarnaPaymentEvent extends Event<KlarnaPaymentEvent> {

    public static final String EVENT_NAME_ON_INITIALIZE = "onInitialized";
    public static final String EVENT_TYPE_NAME_ON_INITIALIZE = "topOnInitialized";
    public static final String EVENT_NAME_ON_LOAD = "onLoaded";
    public static final String EVENT_TYPE_NAME_ON_LOAD = "topOnLoaded";
    public static final String EVENT_NAME_ON_LOAD_PAYMENT_REVIEW = "onLoadedPaymentReview";
    public static final String EVENT_TYPE_NAME_ON_LOAD_PAYMENT_REVIEW = "topOnLoadedPaymentReview";
    public static final String EVENT_NAME_ON_AUTHORIZE = "onAuthorized";
    public static final String EVENT_TYPE_NAME_ON_AUTHORIZE = "topOnAuthorized";
    public static final String EVENT_NAME_ON_REAUTHORIZE = "onReauthorized";
    public static final String EVENT_TYPE_NAME_ON_REAUTHORIZE = "topOnReauthorized";
    public static final String EVENT_NAME_ON_FINALIZE = "onFinalized";
    public static final String EVENT_TYPE_NAME_ON_FINALIZE = "topOnFinalized";
    public static final String EVENT_NAME_ON_ERROR = "onError";
    public static final String EVENT_TYPE_NAME_ON_ERROR = "topOnError";

    @NonNull
    private final String eventName;

    @Nullable
    private final WritableMap additionalParams;

    public KlarnaPaymentEvent(@IdRes int viewId, @NonNull String eventName, @Nullable WritableMap additionalParams) {
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
        if(additionalParams != null){
            return additionalParams;
        }
        return Arguments.createMap();
    }

}
