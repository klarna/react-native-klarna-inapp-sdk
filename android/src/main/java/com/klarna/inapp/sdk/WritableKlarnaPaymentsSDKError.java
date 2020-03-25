package com.klarna.inapp.sdk;

import com.facebook.react.bridge.Arguments;
import com.facebook.react.bridge.WritableMap;
import com.klarna.mobile.sdk.api.payments.KlarnaPaymentsSDKError;

import javax.annotation.Nonnull;

public class WritableKlarnaPaymentsSDKError implements WritableObject {

    @Nonnull
    private KlarnaPaymentsSDKError klarnaPaymentsSDKError;

    WritableKlarnaPaymentsSDKError(@Nonnull KlarnaPaymentsSDKError klarnaPaymentsSDKError) {
        this.klarnaPaymentsSDKError = klarnaPaymentsSDKError;
    }

    @Nonnull
    @Override
    public WritableMap toWritableMap() {
        WritableMap map = Arguments.createMap();
        map.putString("action", klarnaPaymentsSDKError.getAction());
        map.putBoolean("isFatal", klarnaPaymentsSDKError.isFatal());
        map.putString("message", klarnaPaymentsSDKError.getMessage());
        map.putString("name", klarnaPaymentsSDKError.getName());
        map.putArray("invalidFields", WritableUtilities.createWritableArray(klarnaPaymentsSDKError.getInvalidFields()));
        return map;
    }
}
