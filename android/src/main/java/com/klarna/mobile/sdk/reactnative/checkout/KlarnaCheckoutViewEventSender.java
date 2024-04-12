package com.klarna.mobile.sdk.reactnative.checkout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.facebook.react.common.MapBuilder;
import com.facebook.react.uimanager.events.EventDispatcher;
import com.klarna.mobile.sdk.KlarnaMobileSDKError;
import com.klarna.mobile.sdk.api.KlarnaProductEvent;
import com.klarna.mobile.sdk.api.checkout.KlarnaCheckoutView;
import com.klarna.mobile.sdk.reactnative.common.event.ComponentEventSender;
import com.klarna.mobile.sdk.reactnative.common.event.KlarnaEventHandlerEventsUtil;
import com.klarna.mobile.sdk.reactnative.payments.KlarnaPaymentEvent;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;

public class KlarnaCheckoutViewEventSender extends ComponentEventSender<KlarnaCheckoutView> {

    KlarnaCheckoutViewEventSender(@NonNull final Map<WeakReference<KlarnaCheckoutView>, EventDispatcher> viewToDispatcher) {
        super(viewToDispatcher);
    }

    @Override
    public Collection<String> getCallbackEventNames() {
        return Arrays.asList(KlarnaEventHandlerEventsUtil.EVENT_NAME_ON_EVENT, KlarnaEventHandlerEventsUtil.EVENT_NAME_ON_ERROR);
    }

    public void sendKlarnaProductEvent(@Nullable KlarnaCheckoutView view, @NonNull KlarnaProductEvent klarnaProductEvent) {
        KlarnaEventHandlerEventsUtil.sendKlarnaProductEvent(this, view, klarnaProductEvent);
    }

    public void sendKlarnaMobileSDKError(@Nullable KlarnaCheckoutView view, @NonNull KlarnaMobileSDKError klarnaMobileSDKError) {
        KlarnaEventHandlerEventsUtil.sendKlarnaMobileSDKError(this, view, klarnaMobileSDKError);
    }
}
