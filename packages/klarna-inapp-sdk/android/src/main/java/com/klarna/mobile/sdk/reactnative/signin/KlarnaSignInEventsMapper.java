package com.klarna.mobile.sdk.reactnative.signin;

import com.klarna.mobile.sdk.api.signin.model.KlarnaSignInToken;
import com.klarna.mobile.sdk.reactnative.signin.model.KlarnaSignInTokenSerializable;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class KlarnaSignInEventsMapper {
    public static String mapSignInErrorName(String key) {
        Map<String, String> errorsMap = new HashMap<>();
        errorsMap.put("InvalidReturnUrlError", "KlarnaSignInInvalidReturnURL");
        errorsMap.put("KlarnaSignInErrorInvalidCustomTabsReturnUrl", "KlarnaSignInInvalidReturnURL");
        errorsMap.put("KlarnaSignInErrorAlreadyInProgress", "KlarnaSignInAlreadyInProgress");
        errorsMap.put("KlarnaSignInErrorSignInFailed", "KlarnaSignInAuthorizationFailed");
        errorsMap.put("KlarnaSignInErrorInvalidClientID", "KlarnaSignInInvalidClientID");
        errorsMap.put("KlarnaSignInErrorInvalidMarket", "KlarnaSignInInvalidMarket");
        errorsMap.put("klarnaSignInInvalidPresentationContext", "KlarnaSignInInvalidPresentationContext");
        errorsMap.put("KlarnaSignInErrorInvalidScope", "KlarnaSignInInvalidScope");
        errorsMap.put("KlarnaSignInErrorMissingTokenizationCallback", "KlarnaSignInMissingTokenizationId");
        if (errorsMap.get(key) == null) {
            return key;
        }
        return errorsMap.get(key);
    }

    private static final Map<String, String> EVENTS_MAP = new HashMap<>() {
        {
            put("KlarnaSignInToken", "KlarnaSignInToken");
            put("KlarnaSignInUserCancelled", "KlarnaSignInUserCancelled");
        }
    };

    public static String mapSignInEventName(String key) {
        if (EVENTS_MAP.get(key) == null) {
            return key;
        }
        return EVENTS_MAP.get(key);
    }

    private static final Map<String, String> PARAMS_MAP = Collections.singletonMap("KlarnaSignInToken", "KlarnaSignInToken");

    public static String mapSignInParamName(String key) {
        if (EVENTS_MAP.containsKey(key)) {
            return PARAMS_MAP.get(key);
        }
        return key;
    }

    private static final Map<String, ValueTransformer<Object>> TRANSFORMER_MAP = Collections.singletonMap("KlarnaSignInToken", value -> {
        if (value instanceof KlarnaSignInToken) {
            return KlarnaSignInTokenSerializable.Companion.from((KlarnaSignInToken) value);
        }
        return value;
    });

    public static Object transformSignInParamValue(String key, Object value) {
        ValueTransformer<Object> transformer = TRANSFORMER_MAP.get(key);
        if (transformer != null) {
            return transformer.transform(value);
        }
        return value;
    }

    public interface ValueTransformer<T> {
        T transform(Object value);
    }
}
