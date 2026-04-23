package com.klarna.mobile.sdk.reactnative.osm;

import androidx.annotation.NonNull;

import com.klarna.mobile.sdk.KlarnaMobileSDKError;
import com.klarna.mobile.sdk.api.KlarnaEventHandler;
import com.klarna.mobile.sdk.api.KlarnaProductEvent;
import com.klarna.mobile.sdk.api.component.KlarnaComponent;
import com.klarna.mobile.sdk.api.osm.KlarnaOSMView;

public class KlarnaOSMViewEventHandler implements KlarnaEventHandler {

    private final KlarnaOSMViewEventSender eventSender;

    public KlarnaOSMViewEventHandler(@NonNull KlarnaOSMViewEventSender eventSender) {
        this.eventSender = eventSender;
    }

    @Override
    public void onEvent(@NonNull KlarnaComponent klarnaComponent, @NonNull KlarnaProductEvent klarnaProductEvent) {
        // OSM view does not emit product events
    }

    @Override
    public void onError(@NonNull KlarnaComponent klarnaComponent, @NonNull KlarnaMobileSDKError klarnaMobileSDKError) {
        if (klarnaComponent instanceof KlarnaOSMView) {
            eventSender.sendKlarnaMobileSDKError((KlarnaOSMView) klarnaComponent, klarnaMobileSDKError);
        }
    }
}
