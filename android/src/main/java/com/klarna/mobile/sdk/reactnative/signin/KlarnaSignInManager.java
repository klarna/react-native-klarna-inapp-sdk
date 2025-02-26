package com.klarna.mobile.sdk.reactnative.signin;

import androidx.annotation.Nullable;

import com.facebook.react.bridge.NativeModule;
import com.facebook.react.bridge.Promise;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.module.model.ReactModuleInfo;
import com.facebook.react.module.model.ReactModuleInfoProvider;
import com.klarna.mobile.sdk.api.KlarnaEnvironment;
import com.klarna.mobile.sdk.api.KlarnaRegion;
import com.klarna.mobile.sdk.api.signin.KlarnaSignInSDK;
import com.klarna.mobile.sdk.reactnative.NativeKlarnaSignInSpec;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class KlarnaSignInManager extends NativeKlarnaSignInSpec {

    private KlarnaSignInSDK signInSDK;
    private KlarnaSignInEventsHandler handler;

    private final ReactApplicationContext context;

    public KlarnaSignInManager(ReactApplicationContext reactContext) {
        super(reactContext);
        context = reactContext;
    }

    @Nullable
    public NativeModule getModule(String name, ReactApplicationContext reactContext) {
        if (name.equals(KlarnaSignInManager.NAME)) {
            return new KlarnaSignInManager(reactContext);
        } else {
            return null;
        }
    }

    public ReactModuleInfoProvider getReactModuleInfoProvider() {
      return () -> {
          final Map<String, ReactModuleInfo> moduleInfos = new HashMap<>();
          moduleInfos.put(
                  KlarnaSignInManager.NAME,
                  new ReactModuleInfo(
                          KlarnaSignInManager.NAME,
                          KlarnaSignInManager.NAME,
                          false, // canOverrideExistingModule
                          false, // needsEagerInit
                          false, // isCxxModule
                          true // isTurboModule
          ));
          return moduleInfos;
      };
    }

    @Override
    public void init(String environment, String region, String returnUrl) {
        handler = new KlarnaSignInEventsHandler();
        KlarnaEnvironment env = KlarnaSignInEventsHandler.environmentFrom(environment);
        KlarnaRegion reg = KlarnaSignInEventsHandler.regionFrom(region);
        signInSDK = new KlarnaSignInSDK(context.getCurrentActivity(), returnUrl, handler, env, reg);
    }

    @Override
    public void signIn(String clientId, String scope, String market, String locale, String tokenizationId, Promise promise) {
        handler.signInPromise = promise;
        signInSDK.signIn(clientId, scope, market, locale, tokenizationId);
    }
}
