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
import com.klarna.mobile.sdk.api.signin.KlarnaSignInEvent;
import com.klarna.mobile.sdk.api.signin.KlarnaSignInSDK;
import com.klarna.mobile.sdk.reactnative.common.event.KlarnaEventHandlerEventsUtil;
import com.klarna.mobile.sdk.reactnative.common.util.ArgumentsUtil;

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
        if (klarnaMobileSDKError.isFatal()) {
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
            switch (klarnaProductEvent.getAction()) {
                case KlarnaSignInEvent.USER_CANCELLED:
                    if (data.promise != null) {
                        ReadableMap eventMap = ArgumentsUtil.createMap(new HashMap<String, Object>() {{
                            put(KlarnaEventHandlerEventsUtil.PARAM_NAME_NAME, klarnaProductEvent.getAction());
                            put(KlarnaEventHandlerEventsUtil.PARAM_NAME_MESSAGE, "User canceled the sign-in process.");
                            put(KlarnaEventHandlerEventsUtil.PARAM_NAME_IS_FATAL, false);
                            put(KlarnaEventHandlerEventsUtil.PARAM_NAME_SESSION_ID, klarnaProductEvent.getSessionId());
                        }});
                        data.promise.resolve(eventMap);
                    }
                    break;
                case KlarnaSignInEvent.SIGN_IN_TOKEN:
                    if (data.promise != null) {
                        KlarnaEventHandlerEventsUtil.sendKlarnaProductEvent(data, klarnaProductEvent);
                    }
                    break;
                default:
                    break;
            }
        }
    }
}
