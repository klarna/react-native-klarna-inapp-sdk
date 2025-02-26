package com.klarna.mobile.sdk.reactnative.signin;

import static com.klarna.mobile.sdk.reactnative.common.util.ParserUtil.gson;

import androidx.annotation.NonNull;

import com.facebook.react.bridge.Promise;
import com.facebook.react.bridge.ReadableMap;
import com.facebook.react.bridge.WritableMap;
import com.klarna.mobile.sdk.KlarnaMobileSDKError;
import com.klarna.mobile.sdk.api.KlarnaEnvironment;
import com.klarna.mobile.sdk.api.KlarnaEventHandler;
import com.klarna.mobile.sdk.api.KlarnaProductEvent;
import com.klarna.mobile.sdk.api.KlarnaRegion;
import com.klarna.mobile.sdk.api.component.KlarnaComponent;
import com.klarna.mobile.sdk.reactnative.common.util.ArgumentsUtil;
import com.klarna.mobile.sdk.reactnative.standalonewebview.KlarnaStandaloneWebViewEvent;

import java.util.HashMap;

public class KlarnaSignInEventsHandler implements KlarnaEventHandler {

    private static final String PARAM_NAME_ACTION = "action";
    private static final String PARAM_NAME_KLARNA_MESSAGE_EVENT = "klarnaMessageEvent";
    private static final String PARAM_NAME_PARAMS = "params";

    public Promise signInPromise;

    public static KlarnaEnvironment environmentFrom(@NonNull String value) {
        if (value.equals("playground")) {
            return KlarnaEnvironment.PLAYGROUND;
        }

        if (value.equals("staging")) {
            return KlarnaEnvironment.STAGING;
        }

        if (value.equals("production")) {
            return KlarnaEnvironment.PRODUCTION;
        }

        return KlarnaEnvironment.PRODUCTION;
    }

    public static KlarnaRegion regionFrom(@NonNull String value) {
        if (value.equals("eu")) {
            return KlarnaRegion.EU;
        }

        if (value.equals("na")) {
            return KlarnaRegion.NA;
        }

        if (value.equals("oc")) {
            return KlarnaRegion.OC;
        }

        return KlarnaRegion.EU;
    }

    @Override
    public void onError(@NonNull KlarnaComponent klarnaComponent, @NonNull KlarnaMobileSDKError klarnaMobileSDKError) {
        String paramsJson = "{}";
        try {
            paramsJson = gson.toJson(klarnaMobileSDKError.getParams());
        } catch (Exception ignored) {
        }
        String finalParamsJson = paramsJson;
        ReadableMap eventMap = ArgumentsUtil.createMap(new HashMap<String, Object>() {{
            put(PARAM_NAME_ACTION, klarnaMobileSDKError.getName());
            put(PARAM_NAME_PARAMS, finalParamsJson);
        }});
        WritableMap params = ArgumentsUtil.createMap(new HashMap<String, Object>() {{
            put(PARAM_NAME_KLARNA_MESSAGE_EVENT, eventMap);
        }});
        if (signInPromise != null) {
            signInPromise.resolve(params);
        }
    }

    @Override
    public void onEvent(@NonNull KlarnaComponent klarnaComponent, @NonNull KlarnaProductEvent klarnaProductEvent) {
        String paramsJson = "{}";
        try {
            paramsJson = gson.toJson(klarnaProductEvent.getParams());
        } catch (Exception ignored) {
        }
        String finalParamsJson = paramsJson;
        ReadableMap eventMap = ArgumentsUtil.createMap(new HashMap<String, Object>() {{
            put(PARAM_NAME_ACTION, klarnaProductEvent.getAction());
            put(PARAM_NAME_PARAMS, finalParamsJson);
        }});
        WritableMap params = ArgumentsUtil.createMap(new HashMap<String, Object>() {{
            put(PARAM_NAME_KLARNA_MESSAGE_EVENT, eventMap);
        }});
        if (signInPromise != null) {
            signInPromise.resolve(params);
        }
    }
}
