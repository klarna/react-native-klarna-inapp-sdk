package com.klarna.mobile.sdk.reactnative.signin;

import static com.klarna.mobile.sdk.reactnative.common.util.ParserUtil.gson;

import android.util.Log;

import androidx.annotation.NonNull;

import com.facebook.react.bridge.Promise;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactMethod;
import com.facebook.react.bridge.ReadableMap;
import com.facebook.react.bridge.WritableMap;
import com.facebook.react.bridge.WritableNativeMap;
import com.klarna.mobile.sdk.KlarnaMobileSDKError;
import com.klarna.mobile.sdk.api.KlarnaEnvironment;
import com.klarna.mobile.sdk.api.KlarnaEventHandler;
import com.klarna.mobile.sdk.api.KlarnaLoggingLevel;
import com.klarna.mobile.sdk.api.KlarnaMobileSDKCommon;
import com.klarna.mobile.sdk.api.KlarnaProductEvent;
import com.klarna.mobile.sdk.api.KlarnaRegion;
import com.klarna.mobile.sdk.api.component.KlarnaComponent;
import com.klarna.mobile.sdk.api.signin.KlarnaSignInEvent;
import com.klarna.mobile.sdk.api.signin.KlarnaSignInSDK;
import com.klarna.mobile.sdk.api.signin.model.KlarnaSignInToken;
import com.klarna.mobile.sdk.reactnative.common.util.ArgumentsUtil;
import com.klarna.mobile.sdk.reactnative.common.util.ParserUtil;

import java.util.HashMap;
import java.util.Map;

import okhttp3.internal.Util;

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
        if (signInPromise != null) {
            String json = ParserUtil.toJson(klarnaMobileSDKError.getParams());
            WritableMap map = ArgumentsUtil.createMap(klarnaMobileSDKError.getParams());
            signInPromise.reject(klarnaMobileSDKError.getName(), "An error occurred during sign in", map);
        }
    }

    @Override
    public void onEvent(@NonNull KlarnaComponent klarnaComponent, @NonNull KlarnaProductEvent klarnaProductEvent) {
        switch (klarnaProductEvent.getAction()) {
            case KlarnaSignInEvent.USER_CANCELLED:
                if (signInPromise != null) {
                    WritableMap errorMap = new WritableNativeMap();
                    errorMap.putString("sessionId", klarnaProductEvent.getSessionId());
                    signInPromise.reject(KlarnaSignInEvent.USER_CANCELLED, "User cancelled the sign in process", errorMap);
                }
                break;
            case KlarnaSignInEvent.SIGN_IN_TOKEN:
                if (signInPromise != null) {
                    Map<String, Object> params = klarnaProductEvent.getParams();
                    String json = ParserUtil.toJson(params.get(KlarnaSignInEvent.SIGN_IN_TOKEN));
                    signInPromise.resolve(json);
                }
                break;
            default:
                break;
        }
    }
}
