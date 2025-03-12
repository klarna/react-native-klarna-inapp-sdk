package com.klarna.mobile.sdk.reactnative.signin;

import static com.klarna.mobile.sdk.reactnative.common.util.ParserUtil.gson;

import androidx.annotation.NonNull;

import com.facebook.react.bridge.Promise;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReadableMap;
import com.facebook.react.bridge.WritableMap;
import com.klarna.mobile.sdk.KlarnaMobileSDKError;
import com.klarna.mobile.sdk.api.KlarnaEnvironment;
import com.klarna.mobile.sdk.api.KlarnaEventHandler;
import com.klarna.mobile.sdk.api.KlarnaProductEvent;
import com.klarna.mobile.sdk.api.KlarnaRegion;
import com.klarna.mobile.sdk.api.component.KlarnaComponent;
import com.klarna.mobile.sdk.api.signin.KlarnaSignInEvent;
import com.klarna.mobile.sdk.api.signin.KlarnaSignInSDK;
import com.klarna.mobile.sdk.reactnative.common.util.ArgumentsUtil;

import java.util.ArrayList;
import java.util.HashMap;

public class KlarnaSignInModuleImpl implements KlarnaEventHandler {

    private static final String PARAM_NAME_ACTION = "action";
    private static final String PARAM_NAME_KLARNA_MESSAGE_EVENT = "klarnaMessageEvent";
    private static final String PARAM_NAME_PARAMS = "params";

    public static final String NAME = "RNKlarnaSignIn";

    private final ReactApplicationContext reactAppContext;
    private final ArrayList<KlarnaSignInData> signInSDKList;

    public KlarnaSignInModuleImpl(ReactApplicationContext reactAppContext) {
        this.reactAppContext = reactAppContext;
        this.signInSDKList = new ArrayList<KlarnaSignInData>();
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
        for (KlarnaSignInData data : signInSDKList) {
            if (data.sdkInstance == component) {
                return data;
            }
        }
        return null;
    }

    private KlarnaSignInData getInstanceData(String instanceId) {
        for (KlarnaSignInData data : signInSDKList) {
            if (data.instanceId.equals(instanceId)) {
                return data;
            }
        }
        return null;
    }

    private String getParamsFrom(KlarnaProductEvent event) {
        String paramsJson = "{}";
        try {
            paramsJson = gson.toJson(event.getParams());
        } catch (Exception ignored) {
        }
        return paramsJson;
    }

    /* Module public methods */
    public void init(String instanceId, String environment, String region, String returnUrl) {
        KlarnaEnvironment env = environmentFrom(environment);
        KlarnaRegion reg = regionFrom(region);
        reactAppContext.runOnUiQueueThread(() -> {
            KlarnaSignInSDK signInInstance = new KlarnaSignInSDK(reactAppContext.getCurrentActivity(), returnUrl, this, env, reg);
            KlarnaSignInData signInData = new KlarnaSignInData(instanceId, signInInstance);
            signInSDKList.add(signInData);
        });
    }

    public void signIn(String instanceId, String clientId, String scope, String market, String locale, String tokenizationId, Promise promise) {
        KlarnaSignInData signInData = getInstanceData(instanceId);
        if (signInData != null) {
            signInData.promise = promise;
            signInData.sdkInstance.signIn(clientId, scope, market, locale, tokenizationId);
        } else {
            promise.reject("SIGN_IN_SDK_NOT_INITIALIZED", "Sign in SDK not initialized");
        }
    }

    /* KlarnaEventHandler methods */

    @Override
    public void onError(@NonNull KlarnaComponent klarnaComponent, @NonNull KlarnaMobileSDKError klarnaMobileSDKError) {
        // Ignore not fatal errors
        if (!klarnaMobileSDKError.isFatal()) {
            return;
        }
        KlarnaSignInData data = getInstanceData(klarnaComponent);
        if (data != null) {
            if (data.promise != null) {
                WritableMap map = ArgumentsUtil.createMap(klarnaMobileSDKError.getParams());
                data.promise.reject(klarnaMobileSDKError.getName(), klarnaMobileSDKError.getMessage(), map);
                signInSDKList.remove(data);
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
                            put(PARAM_NAME_ACTION, klarnaProductEvent.getAction());
                            put(PARAM_NAME_PARAMS, getParamsFrom(klarnaProductEvent));
                        }});
                        WritableMap errorMap = ArgumentsUtil.createMap(new HashMap<String, Object>() {{
                            put(PARAM_NAME_KLARNA_MESSAGE_EVENT, eventMap);
                        }});
                        errorMap.putString("sessionId", klarnaProductEvent.getSessionId());
                        data.promise.reject(klarnaProductEvent.getAction(), errorMap);
                        signInSDKList.remove(data);
                    }
                    break;
                case KlarnaSignInEvent.SIGN_IN_TOKEN:
                    if (data.promise != null) {
                        ReadableMap event = ArgumentsUtil.createMap(new HashMap<String, Object>() {{
                            put(PARAM_NAME_ACTION, klarnaProductEvent.getAction());
                            put(PARAM_NAME_PARAMS, getParamsFrom(klarnaProductEvent));
                        }});
                        data.promise.resolve(event);
                        signInSDKList.remove(data);
                    }
                    break;
                default:
                    break;
            }
        }
    }
}
