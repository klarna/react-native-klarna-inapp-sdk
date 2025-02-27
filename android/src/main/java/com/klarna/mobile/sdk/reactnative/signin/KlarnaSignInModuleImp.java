package com.klarna.mobile.sdk.reactnative.signin;

import static com.klarna.mobile.sdk.reactnative.common.util.ParserUtil.gson;

import androidx.annotation.NonNull;

import com.facebook.react.bridge.Promise;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactMethod;
import com.facebook.react.bridge.ReadableMap;
import com.facebook.react.bridge.WritableMap;
import com.klarna.mobile.sdk.KlarnaMobileSDKError;
import com.klarna.mobile.sdk.api.KlarnaEnvironment;
import com.klarna.mobile.sdk.api.KlarnaEventHandler;
import com.klarna.mobile.sdk.api.KlarnaLoggingLevel;
import com.klarna.mobile.sdk.api.KlarnaMobileSDKCommon;
import com.klarna.mobile.sdk.api.KlarnaProductEvent;
import com.klarna.mobile.sdk.api.KlarnaRegion;
import com.klarna.mobile.sdk.api.component.KlarnaComponent;
import com.klarna.mobile.sdk.api.signin.KlarnaSignInSDK;
import com.klarna.mobile.sdk.reactnative.common.util.ArgumentsUtil;

import java.util.HashMap;

public class KlarnaSignInModuleImp implements KlarnaEventHandler {

    private static final String PARAM_NAME_ACTION = "action";
    private static final String PARAM_NAME_KLARNA_MESSAGE_EVENT = "klarnaMessageEvent";
    private static final String PARAM_NAME_PARAMS = "params";

    public Promise signInPromise;

    public static final String NAME = "RNKlarnaSignIn";

    private final ReactApplicationContext reactAppContext;
    private KlarnaSignInSDK signInSDK;

    public KlarnaSignInModuleImp(ReactApplicationContext reactAppContext) {
        this.reactAppContext = reactAppContext;
        KlarnaMobileSDKCommon.setLoggingLevel(KlarnaLoggingLevel.Verbose);
    }

    /* Module private methods */

    private KlarnaEnvironment environmentFrom(@NonNull String value) {
        return switch (value) {
            case "playground" -> KlarnaEnvironment.PLAYGROUND;
            case "staging" -> KlarnaEnvironment.STAGING;
            default -> KlarnaEnvironment.PRODUCTION;
        };

    }

    private KlarnaRegion regionFrom(@NonNull String value) {
        return switch (value) {
            case "na" -> KlarnaRegion.NA;
            case "oc" -> KlarnaRegion.OC;
            default -> KlarnaRegion.EU;
        };

    }

    /* Module public methods */

    public void init(String environment, String region, String returnUrl) {
        KlarnaEnvironment env = environmentFrom(environment);
        KlarnaRegion reg = regionFrom(region);
        reactAppContext.runOnUiQueueThread(() -> {
            signInSDK = new KlarnaSignInSDK(reactAppContext.getCurrentActivity(), returnUrl, this, env, reg);
        });
    }

    public void signIn(String clientId, String scope, String market, String locale, String tokenizationId, Promise promise) {
        this.signInPromise = promise;
        signInSDK.signIn(clientId, scope, market, locale, tokenizationId);
    }

    /* KlarnaEventHandler methods */

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
