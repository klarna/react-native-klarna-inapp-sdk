package com.klarna.mobile.sdk.reactnative.common.event;

import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.facebook.react.bridge.Promise;
import com.facebook.react.bridge.ReadableMap;
import com.facebook.react.bridge.WritableMap;
import com.klarna.mobile.sdk.KlarnaMobileSDKError;
import com.klarna.mobile.sdk.api.KlarnaProductEvent;
import com.klarna.mobile.sdk.reactnative.common.util.ArgumentsUtil;
import com.klarna.mobile.sdk.reactnative.common.util.ParserUtil;
import com.klarna.mobile.sdk.reactnative.signin.KlarnaSignInData;

import java.util.HashMap;

public class KlarnaEventHandlerEventsUtil {

    public static final String EVENT_NAME_ON_EVENT = "onEvent";
    public static final String EVENT_NAME_ON_ERROR = "onError";
    public static final String PARAM_NAME_IS_FATAL = "isFatal";
    public static final String PARAM_NAME_MESSAGE = "message";
    public static final String PARAM_NAME_NAME = "name";
    public static final String PARAM_NAME_SESSION_ID = "sessionId";

    private static final String PARAM_NAME_ACTION = "action";
    private static final String PARAM_NAME_PARAMS = "params";
    private static final String PARAM_NAME_PRODUCT_EVENT = "productEvent";

    public static <T extends View> void sendKlarnaProductEvent(@NonNull ComponentEventSender<T> componentEventSender, @Nullable View view, @NonNull KlarnaProductEvent klarnaProductEvent) {
        String stringifiedParams = ParserUtil.toJson(klarnaProductEvent.getParams());
        String paramsJson = stringifiedParams == null ? "{}" : stringifiedParams;
        ReadableMap eventMap = ArgumentsUtil.createMap(new HashMap<String, Object>() {{
            put(PARAM_NAME_ACTION, klarnaProductEvent.getAction());
            put(PARAM_NAME_PARAMS, paramsJson);
        }});
        WritableMap params = ArgumentsUtil.createMap(new HashMap<String, Object>() {{
            put(PARAM_NAME_PRODUCT_EVENT, eventMap);
        }});
        componentEventSender.postEventForView(view, EVENT_NAME_ON_EVENT, params);
    }

    public static <T extends View> void sendKlarnaMobileSDKError(@NonNull ComponentEventSender<T> componentEventSender, @Nullable View view, @NonNull KlarnaMobileSDKError klarnaMobileSDKError) {
        ReadableMap eventMap = ArgumentsUtil.createMap(new HashMap<String, Object>() {{
            put(PARAM_NAME_NAME, klarnaMobileSDKError.getName());
            put(PARAM_NAME_MESSAGE, klarnaMobileSDKError.getMessage());
            put(PARAM_NAME_IS_FATAL, klarnaMobileSDKError.isFatal());
        }});
        WritableMap params = ArgumentsUtil.createMap(new HashMap<String, Object>() {{
            put(PARAM_NAME_PRODUCT_EVENT, eventMap);
        }});
        componentEventSender.postEventForView(view, EVENT_NAME_ON_EVENT, params);
    }

    public static void sendKlarnaProductEvent(@NonNull KlarnaSignInData signInData, @NonNull KlarnaProductEvent klarnaProductEvent) {
        String stringifiedParams = ParserUtil.toJson(klarnaProductEvent.getParams());
        String paramsJson = stringifiedParams == null ? "{}" : stringifiedParams;
        ReadableMap eventMap = ArgumentsUtil.createMap(new HashMap<String, Object>() {{
            put(PARAM_NAME_ACTION, klarnaProductEvent.getAction());
            put(PARAM_NAME_SESSION_ID, klarnaProductEvent.getSessionId());
            put(PARAM_NAME_PARAMS, paramsJson);
        }});
        WritableMap params = ArgumentsUtil.createMap(new HashMap<String, Object>() {{
            put(PARAM_NAME_PRODUCT_EVENT, eventMap);
        }});
        if (signInData.promise != null) {
            signInData.promise.reject(EVENT_NAME_ON_EVENT, params);
        }
    }

    public static void sendKlarnaMobileSDKError(@NonNull KlarnaSignInData signInData, @NonNull KlarnaMobileSDKError klarnaMobileSDKError) {
        WritableMap eventMap = ArgumentsUtil.createMap(new HashMap<String, Object>() {{
            put(PARAM_NAME_NAME, klarnaMobileSDKError.getName());
            put(PARAM_NAME_MESSAGE, klarnaMobileSDKError.getMessage());
            put(PARAM_NAME_IS_FATAL, klarnaMobileSDKError.isFatal());
            put(PARAM_NAME_SESSION_ID, klarnaMobileSDKError.getSessionId());
        }});
        if (signInData.promise != null) {
            signInData.promise.reject(EVENT_NAME_ON_ERROR, eventMap);
        }
    }
}
