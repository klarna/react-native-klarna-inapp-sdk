package com.klarna.mobile.sdk.reactnative.checkout;

import androidx.annotation.NonNull;

import com.klarna.mobile.sdk.KlarnaMobileSDKError;
import com.klarna.mobile.sdk.api.KlarnaEventHandler;
import com.klarna.mobile.sdk.api.KlarnaProductEvent;
import com.klarna.mobile.sdk.api.checkout.KlarnaCheckoutView;
import com.klarna.mobile.sdk.api.component.KlarnaComponent;

public class KlarnaCheckoutViewEventHandler implements KlarnaEventHandler {

    private final KlarnaCheckoutViewEventSender klarnaCheckoutViewEventSender;

    public KlarnaCheckoutViewEventHandler(KlarnaCheckoutViewEventSender klarnaCheckoutViewEventSender) {
        this.klarnaCheckoutViewEventSender = klarnaCheckoutViewEventSender;
    }

    @Override
    public void onEvent(@NonNull KlarnaComponent klarnaComponent, @NonNull KlarnaProductEvent klarnaProductEvent) {
        if (klarnaComponent instanceof KlarnaCheckoutView) {
            klarnaCheckoutViewEventSender.sendKlarnaProductEvent((KlarnaCheckoutView) klarnaComponent, klarnaProductEvent);
        }
    }

    @Override
    public void onError(@NonNull KlarnaComponent klarnaComponent, @NonNull KlarnaMobileSDKError klarnaMobileSDKError) {
        if (klarnaComponent instanceof KlarnaCheckoutView) {
            klarnaCheckoutViewEventSender.sendKlarnaMobileSDKError((KlarnaCheckoutView) klarnaComponent, klarnaMobileSDKError);
        }
    }
}
