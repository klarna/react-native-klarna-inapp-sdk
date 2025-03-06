package com.klarna.mobile.sdk.reactnative.spec;

import androidx.annotation.Nullable;

import com.facebook.react.bridge.NativeModule;
import com.facebook.react.bridge.Promise;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.module.model.ReactModuleInfo;
import com.facebook.react.module.model.ReactModuleInfoProvider;
import com.klarna.mobile.sdk.reactnative.NativeKlarnaSignInSpec;
import com.klarna.mobile.sdk.reactnative.signin.KlarnaSignInModuleImp;

import java.util.HashMap;
import java.util.Map;

public class RNKlarnaSignInModuleSpec extends NativeKlarnaSignInSpec {

    private final KlarnaSignInModuleImp module;

    public RNKlarnaSignInModuleSpec(ReactApplicationContext reactContext) {
        super(reactContext);
        this.module = new KlarnaSignInModuleImp(reactContext);
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
    public void init(String environment, String region, String returnUrl) {
        module.init(environment, region, returnUrl);
    }

    @Override
    public void signIn(String clientId, String scope, String market, String locale, String tokenizationId, Promise promise) {
        module.signIn(clientId, scope, market, locale, tokenizationId, promise);
    }
}
