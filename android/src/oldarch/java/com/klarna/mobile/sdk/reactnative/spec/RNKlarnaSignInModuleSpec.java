package com.klarna.mobile.sdk.reactnative.spec;

import androidx.annotation.NonNull;

import com.facebook.react.bridge.Promise;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;
import com.klarna.mobile.sdk.reactnative.signin.KlarnaSignInModuleImpl;

public class RNKlarnaSignInModuleSpec extends ReactContextBaseJavaModule {
    private KlarnaSignInModuleImpl module;

    public RNKlarnaSignInModuleSpec(ReactApplicationContext reactContext) {
        super(reactContext);
        this.module = new KlarnaSignInModuleImpl(reactContext);
    }

    @NonNull
    @Override
    public String getName() {
        return KlarnaSignInModuleImpl.NAME;
    }

    @ReactMethod(isBlockingSynchronousMethod = false)
    public void init(String instanceId, String environment, String region, String returnUrl, Promise promise) {
        module.init(instanceId, environment, region, returnUrl, promise);
    }

    @ReactMethod(isBlockingSynchronousMethod = false)
    public void dispose(String instanceId, Promise promise) {
        module.dispose(instanceId, promise);
    }

    @ReactMethod(isBlockingSynchronousMethod = false)
    public void signIn(String instanceId, String clientId, String scope, String market, String locale, String tokenizationId, Promise promise) {
        module.signIn(instanceId, clientId, scope, market, locale, tokenizationId, promise);
    }
}
