package com.klarna.mobile.sdk.reactnative.common.event;

import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.facebook.react.bridge.ReadableMap;
import com.facebook.react.bridge.WritableMap;
import com.klarna.mobile.sdk.KlarnaMobileSDKError;
import com.klarna.mobile.sdk.api.KlarnaProductEvent;
import com.klarna.mobile.sdk.reactnative.common.serializer.DynamicMapSerializer;
import com.klarna.mobile.sdk.reactnative.common.util.ArgumentsUtil;
import com.klarna.mobile.sdk.reactnative.common.util.ParserUtil;
import com.klarna.mobile.sdk.reactnative.signin.KlarnaSignInData;
import com.klarna.mobile.sdk.reactnative.signin.KlarnaSignInEventsMapper;

import java.util.HashMap;
import java.util.Map;

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
        String stringifiedParams = ParserUtil.INSTANCE.toJson(DynamicMapSerializer.INSTANCE, klarnaProductEvent.getParams());
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
        if (klarnaProductEvent.getAction().equals("KlarnaSignInUserAuth")) {
            return;
        }
        Map<String, Object> updatedParams = new HashMap<>();
        for(Map.Entry<String, Object> entry : klarnaProductEvent.getParams().entrySet()) {
            String updatedKey = KlarnaSignInEventsMapper.mapSignInParamName(entry.getKey());
            updatedParams.put(updatedKey, entry.getValue());
        }
        String eventName = KlarnaSignInEventsMapper.mapSignInEventName(klarnaProductEvent.getAction());
        ReadableMap eventMap = ArgumentsUtil.createMap(new HashMap<String, Object>() {{
            put(PARAM_NAME_ACTION, eventName);
            put(PARAM_NAME_SESSION_ID, klarnaProductEvent.getSessionId());
            if (!updatedParams.isEmpty()) {
                put(PARAM_NAME_PARAMS, ArgumentsUtil.createMapUsingJSONString(updatedParams));
            }
        }});

        if (signInData.promise != null) {
            signInData.promise.resolve(eventMap);
        }
    }

    public static void sendKlarnaMobileSDKError(@NonNull KlarnaSignInData signInData, @NonNull KlarnaMobileSDKError klarnaMobileSDKError) {
        String errorName = KlarnaSignInEventsMapper.mapSignInErrorName(klarnaMobileSDKError.getName());
        WritableMap eventMap = ArgumentsUtil.createMap(new HashMap<String, Object>() {{
            put(PARAM_NAME_NAME, errorName);
            put(PARAM_NAME_MESSAGE, klarnaMobileSDKError.getMessage());
            put(PARAM_NAME_IS_FATAL, klarnaMobileSDKError.isFatal());
            put(PARAM_NAME_SESSION_ID, klarnaMobileSDKError.getSessionId());
            if (!klarnaMobileSDKError.getParams().isEmpty()) {
                put(PARAM_NAME_PARAMS, ArgumentsUtil.createMapUsingJSONString(klarnaMobileSDKError.getParams()));
            }
        }});
        if (signInData.promise != null) {
            signInData.promise.reject(EVENT_NAME_ON_ERROR, eventMap);
        }
    }

    public static ReadableMap createMapFrom(HashMap<String, Object> map) {
        WritableMap writableMap = ArgumentsUtil.createMap(new HashMap<String, Object>());
        for (Map.Entry<String, Object> entry : map.entrySet()) {
            if (entry.getValue() instanceof String) {
                writableMap.putString(entry.getKey(), (String) entry.getValue());
            } else if (entry.getValue() instanceof Object) {

            }
        }
        return ArgumentsUtil.createMap(map);
    }
}
