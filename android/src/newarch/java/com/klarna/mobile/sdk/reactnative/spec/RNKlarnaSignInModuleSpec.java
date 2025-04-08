package com.klarna.mobile.sdk.reactnative.spec;

import androidx.annotation.Nullable;

import com.facebook.react.bridge.NativeModule;
import com.facebook.react.bridge.Promise;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.module.model.ReactModuleInfo;
import com.facebook.react.module.model.ReactModuleInfoProvider;
import com.klarna.mobile.sdk.reactnative.NativeKlarnaSignInSpec;
import com.klarna.mobile.sdk.reactnative.signin.KlarnaSignInModuleImpl;

import java.util.HashMap;
import java.util.Map;

public class RNKlarnaSignInModuleSpec extends NativeKlarnaSignInSpec {

    private final KlarnaSignInModuleImpl module;

    public RNKlarnaSignInModuleSpec(ReactApplicationContext reactContext) {
        super(reactContext);
        this.module = new KlarnaSignInModuleImpl(reactContext);
    }

    @Nullable
    public NativeModule getModule(String name, ReactApplicationContext reactContext) {
        if (name.equals(RNKlarnaSignInModuleSpec.NAME)) {
            return new RNKlarnaSignInModuleSpec(reactContext);
        } else {
            return null;
        }
    }

    public ReactModuleInfoProvider getReactModuleInfoProvider() {
      return () -> {
          final Map<String, ReactModuleInfo> moduleInfos = new HashMap<>();
          moduleInfos.put(
                  RNKlarnaSignInModuleSpec.NAME,
                  new ReactModuleInfo(
                          RNKlarnaSignInModuleSpec.NAME,
                          RNKlarnaSignInModuleSpec.NAME,
                          false, // canOverrideExistingModule
                          false, // needsEagerInit
                          false, // isCxxModule
                          true // isTurboModule
          ));
          return moduleInfos;
      };
    }

    @Override
    public void init(String instanceId, String environment, String region, String returnUrl, Promise promise) {
        module.init(instanceId, environment, region, returnUrl, promise);
    }

    @Override
    public void dispose(String instanceId, Promise promise) {
        module.dispose(instanceId, promise);
    }

    @Override
    public void signIn(String instanceId, String clientId, String scope, String market, String locale, String tokenizationId, Promise promise) {
        module.signIn(instanceId, clientId, scope, market, locale, tokenizationId, promise);
    }
}
