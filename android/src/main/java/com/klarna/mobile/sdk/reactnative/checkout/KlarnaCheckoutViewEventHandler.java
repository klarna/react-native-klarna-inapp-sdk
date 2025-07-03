package com.klarna.mobile.sdk.reactnative.checkout;

import androidx.annotation.NonNull;

import com.klarna.mobile.sdk.KlarnaMobileSDKError;
import com.klarna.mobile.sdk.api.KlarnaEventHandler;
import com.klarna.mobile.sdk.api.KlarnaProductEvent;
import com.klarna.mobile.sdk.api.checkout.KlarnaCheckoutView;
import com.klarna.mobile.sdk.api.component.KlarnaComponent;

public class KlarnaCheckoutViewEventHandler implements KlarnaEventHandler {

    public interface OnLoadListener {
        void onLoad(KlarnaCheckoutView klarnaCheckoutView);
    }

    private static final String EVENT_LOAD = "load";
    private final KlarnaCheckoutViewEventSender klarnaCheckoutViewEventSender;
    private final OnLoadListener onLoadListener;

    public KlarnaCheckoutViewEventHandler(KlarnaCheckoutViewEventSender klarnaCheckoutViewEventSender, OnLoadListener onLoadListener) {
        this.klarnaCheckoutViewEventSender = klarnaCheckoutViewEventSender;
        this.onLoadListener = onLoadListener;
    }

    @Override
    public void onEvent(@NonNull KlarnaComponent klarnaComponent, @NonNull KlarnaProductEvent klarnaProductEvent) {
        if (klarnaComponent instanceof KlarnaCheckoutView) {
            String eventName = klarnaProductEvent.getAction();
            if (EVENT_LOAD.equalsIgnoreCase(eventName)) {
                if (onLoadListener != null) {
                    onLoadListener.onLoad((KlarnaCheckoutView) klarnaComponent);
                }
            }
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
