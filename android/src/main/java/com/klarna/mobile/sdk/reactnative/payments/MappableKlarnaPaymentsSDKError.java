package com.klarna.mobile.sdk.reactnative.payments;

import androidx.annotation.NonNull;

import com.facebook.react.bridge.Arguments;
import com.facebook.react.bridge.ReadableMap;
import com.facebook.react.bridge.WritableArray;
import com.facebook.react.bridge.WritableMap;
import com.klarna.mobile.sdk.api.payments.KlarnaPaymentsSDKError;
import com.klarna.mobile.sdk.reactnative.common.Mappable;

import java.util.List;

class MappableKlarnaPaymentsSDKError implements Mappable {

    @NonNull
    private KlarnaPaymentsSDKError klarnaPaymentsSDKError;

    MappableKlarnaPaymentsSDKError(@NonNull KlarnaPaymentsSDKError klarnaPaymentsSDKError) {
        this.klarnaPaymentsSDKError = klarnaPaymentsSDKError;
    }

    @NonNull
    @Override
    public ReadableMap buildMap() {
        WritableMap map = Arguments.createMap();
        map.putString("action", klarnaPaymentsSDKError.getAction());
        map.putBoolean("isFatal", klarnaPaymentsSDKError.isFatal());
        map.putString("message", klarnaPaymentsSDKError.getMessage());
        map.putString("name", klarnaPaymentsSDKError.getName());
        map.putString("sessionId", klarnaPaymentsSDKError.getSessionId());

        WritableArray invalidFields = Arguments.createArray();
        List<String> errorInvalidFields = klarnaPaymentsSDKError.getInvalidFields();
        if (errorInvalidFields != null && errorInvalidFields.size() > 0) {
            for (int i = 0; i < errorInvalidFields.size(); i++) {
                String field = errorInvalidFields.get(i);
                if (field != null) {
                    invalidFields.pushString(field);
                }
            }
        }
        map.putArray("invalidFields", invalidFields);

        return map;
    }
}
