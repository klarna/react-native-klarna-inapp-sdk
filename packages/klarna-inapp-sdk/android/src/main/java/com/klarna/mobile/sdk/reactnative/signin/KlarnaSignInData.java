package com.klarna.mobile.sdk.reactnative.signin;

import com.facebook.react.bridge.Promise;
import com.klarna.mobile.sdk.api.signin.KlarnaSignInSDK;

public class KlarnaSignInData {
    String instanceId;
    public Promise promise;
    KlarnaSignInSDK sdkInstance;

    public KlarnaSignInData(String instanceId, KlarnaSignInSDK sdkInstance) {
        this.instanceId = instanceId;
        this.sdkInstance = sdkInstance;
    }
}
