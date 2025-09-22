package com.klarna.mobile.sdk.reactnative.signin;

import androidx.annotation.NonNull;

import com.facebook.react.bridge.Promise;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReadableMap;
import com.klarna.mobile.sdk.KlarnaMobileSDKError;
import com.klarna.mobile.sdk.api.KlarnaEnvironment;
import com.klarna.mobile.sdk.api.KlarnaEventHandler;
import com.klarna.mobile.sdk.api.KlarnaProductEvent;
import com.klarna.mobile.sdk.api.KlarnaRegion;
import com.klarna.mobile.sdk.api.component.KlarnaComponent;
import com.klarna.mobile.sdk.api.signin.KlarnaSignInError;
import com.klarna.mobile.sdk.api.signin.KlarnaSignInEvent;
import com.klarna.mobile.sdk.api.signin.KlarnaSignInSDK;
import com.klarna.mobile.sdk.reactnative.common.event.KlarnaEventHandlerEventsUtil;
import com.klarna.mobile.sdk.reactnative.common.util.ArgumentsUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class KlarnaSignInModuleImpl implements KlarnaEventHandler {

    public static final String NAME = "RNKlarnaSignIn";

    public static final String SIGN_IN_SDK_NOT_INITIALIZED = "SIGN_IN_SDK_NOT_INITIALIZED";

    private final ReactApplicationContext reactAppContext;
    private final HashMap<String, KlarnaSignInData> signInSDKMap;

    public KlarnaSignInModuleImpl(ReactApplicationContext reactAppContext) {
        this.reactAppContext = reactAppContext;
        this.signInSDKMap = new HashMap<String, KlarnaSignInData>();
    }

    /* Module private methods */

    private KlarnaEnvironment environmentFrom(@NonNull String value) {
        switch (value) {
            case "playground":
                return KlarnaEnvironment.PLAYGROUND;
            case "staging":
                return KlarnaEnvironment.STAGING;
            default:
                return KlarnaEnvironment.PRODUCTION;
        }
    }

    private KlarnaRegion regionFrom(@NonNull String value) {
        switch (value) {
            case "na":
                return KlarnaRegion.NA;
            case "oc":
                return KlarnaRegion.OC;
            default:
                return KlarnaRegion.EU;
        }
    }

    private KlarnaSignInData getInstanceData(KlarnaComponent component) {
        for (Map.Entry<String, KlarnaSignInData> entry : signInSDKMap.entrySet()) {
            KlarnaSignInData data = entry.getValue();
            if (data.sdkInstance == component) {
                return data;
            }
        }
        return null;
    }

    /* Module public methods */
    public void init(String instanceId, String environment, String region, String returnUrl, Promise promise) {
        KlarnaEnvironment env = environmentFrom(environment);
        KlarnaRegion reg = regionFrom(region);
        reactAppContext.runOnUiQueueThread(() -> {
            KlarnaSignInSDK signInInstance = new KlarnaSignInSDK(reactAppContext.getCurrentActivity(), returnUrl, this, env, reg);
            KlarnaSignInData signInData = new KlarnaSignInData(instanceId, signInInstance);
            signInSDKMap.put(instanceId, signInData);
            promise.resolve(null);
        });
    }

    public void signIn(String instanceId, String clientId, String scope, String market, String locale, String tokenizationId, Promise promise) {
        KlarnaSignInData signInData = signInSDKMap.get(instanceId);
        if (signInData != null) {
            signInData.promise = promise;
            signInData.sdkInstance.signIn(clientId, scope, market, locale, tokenizationId);
        } else {
            promise.reject(SIGN_IN_SDK_NOT_INITIALIZED, "Sign in SDK not initialized");
        }
    }

    public void dispose(String instanceId, Promise promise) {
        KlarnaSignInData signInData = signInSDKMap.get(instanceId);
        if (signInData != null) {
            signInSDKMap.remove(instanceId);
            promise.resolve(null);
        }
    }

    /* KlarnaEventHandler methods */

    @Override
    public void onError(@NonNull KlarnaComponent klarnaComponent, @NonNull KlarnaMobileSDKError klarnaMobileSDKError) {
        ArrayList<String> allowedNonFatalErrors = new ArrayList<>();
        allowedNonFatalErrors.add(KlarnaSignInError.SignInFailed);
        allowedNonFatalErrors.add(KlarnaSignInError.AlreadyInProgress);
        if (klarnaMobileSDKError.isFatal() || !allowedNonFatalErrors.contains(klarnaMobileSDKError.getName())) {
            KlarnaSignInData data = getInstanceData(klarnaComponent);
            if (data != null) {
                if (data.promise != null) {
                    KlarnaEventHandlerEventsUtil.sendKlarnaMobileSDKError(data, klarnaMobileSDKError);
                }
            }
        }
    }

    @Override
    public void onEvent(@NonNull KlarnaComponent klarnaComponent, @NonNull KlarnaProductEvent klarnaProductEvent) {
        KlarnaSignInData data = getInstanceData(klarnaComponent);
        if (data != null) {
            if (data.promise != null) {
                KlarnaEventHandlerEventsUtil.sendKlarnaProductEvent(data, klarnaProductEvent);
            }
        }
    }
}
