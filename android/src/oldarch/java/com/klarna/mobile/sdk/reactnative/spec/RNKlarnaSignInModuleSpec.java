package com.klarna.mobile.sdk.reactnative.spec;

import androidx.annotation.NonNull;

import com.facebook.react.bridge.Promise;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;
import com.klarna.mobile.sdk.reactnative.signin.KlarnaSignInModuleImp;

public class RNKlarnaSignInModuleSpec extends ReactContextBaseJavaModule {
    private KlarnaSignInModuleImp module;

    public RNKlarnaSignInModuleSpec(ReactApplicationContext reactContext) {
        super(reactContext);
        this.module = new KlarnaSignInModuleImp(reactContext);
    }

    @NonNull
    @Override
    public String getName() {
        return KlarnaSignInModuleImp.NAME;
    }

    @ReactMethod(isBlockingSynchronousMethod = true)
    public void init(String environment, String region, String returnUrl) {
        module.init(environment, region, returnUrl);
    }

    @ReactMethod(isBlockingSynchronousMethod = false)
    public void signIn(String clientId, String scope, String market, String locale, String tokenizationId, Promise promise) {
        module.signIn(clientId, scope, market, locale, tokenizationId, promise);
    }
}
