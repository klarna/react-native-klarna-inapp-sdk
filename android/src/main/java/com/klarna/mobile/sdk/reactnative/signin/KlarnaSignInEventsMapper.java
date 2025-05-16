package com.klarna.mobile.sdk.reactnative.signin;

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

    public static String mapSignInEventName(String key) {
        Map<String, String> eventsMap = new HashMap<>();
        eventsMap.put("KlarnaSignInToken", "KlarnaSignInToken");
        eventsMap.put("KlarnaSignInUserCancelled", "KlarnaSignInUserCancelled");
        if (eventsMap.get(key) == null) {
            return key;
        }
        return eventsMap.get(key);
    }

    public static String mapSignInParamName(String key) {
        Map<String, String> paramsMap = new HashMap<>();
        paramsMap.put("KlarnaSignInToken", "KlarnaSignInToken");
        return paramsMap.get(key);
    }
}
