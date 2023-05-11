package com.klarna.inapp.sdk;

import androidx.annotation.NonNull;

import com.facebook.react.common.MapBuilder;
import com.klarna.mobile.sdk.api.payments.KlarnaPaymentsSDKError;

import java.util.Map;

class MappableKlarnaPaymentsSDKError implements Mappable {

    @NonNull
    private KlarnaPaymentsSDKError klarnaPaymentsSDKError;

    MappableKlarnaPaymentsSDKError(@NonNull KlarnaPaymentsSDKError klarnaPaymentsSDKError) {
        this.klarnaPaymentsSDKError = klarnaPaymentsSDKError;
    }

    @NonNull
    @Override
    public Map<String, Object> buildMap() {
        return MapBuilder.<String, Object>builder()
                .put("action", klarnaPaymentsSDKError.getAction())
                .put("isFatal", klarnaPaymentsSDKError.isFatal())
                .put("message", klarnaPaymentsSDKError.getMessage())
                .put("name", klarnaPaymentsSDKError.getName())
                .put("invalidFields", klarnaPaymentsSDKError.getInvalidFields())
                .put("sessionId", klarnaPaymentsSDKError.getSessionId())
                .build();
    }
}
