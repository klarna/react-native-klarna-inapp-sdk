package com.klarna.mobile.sdk.reactnative.checkout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.facebook.react.bridge.WritableMap;
import com.facebook.react.uimanager.events.EventDispatcher;
import com.klarna.mobile.sdk.KlarnaMobileSDKError;
import com.klarna.mobile.sdk.api.KlarnaProductEvent;
import com.klarna.mobile.sdk.api.checkout.KlarnaCheckoutView;
import com.klarna.mobile.sdk.reactnative.common.event.ComponentEvent;
import com.klarna.mobile.sdk.reactnative.common.event.ComponentEventSender;
import com.klarna.mobile.sdk.reactnative.common.event.KlarnaEventHandlerEventsUtil;
import com.klarna.mobile.sdk.reactnative.common.ui.WrapperView;
import com.klarna.mobile.sdk.reactnative.common.util.ArgumentsUtil;

import java.lang.ref.WeakReference;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class KlarnaCheckoutViewEventSender extends ComponentEventSender<WrapperView<KlarnaCheckoutView>> {

    private static final String EVENT_NAME_ON_RESIZED = "onResized";

    KlarnaCheckoutViewEventSender(@NonNull final Map<WeakReference<WrapperView<KlarnaCheckoutView>>, EventDispatcher> viewToDispatcher) {
        super(viewToDispatcher);
    }

    @Override
    public Collection<String> getCallbackEventNames() {
        return Arrays.asList(
                KlarnaEventHandlerEventsUtil.EVENT_NAME_ON_EVENT,
                KlarnaEventHandlerEventsUtil.EVENT_NAME_ON_ERROR,
                EVENT_NAME_ON_RESIZED
        );
    }

    public void sendKlarnaProductEvent(@Nullable KlarnaCheckoutView view, @NonNull KlarnaProductEvent klarnaProductEvent) {
        KlarnaEventHandlerEventsUtil.sendKlarnaProductEvent(this, view, klarnaProductEvent);
    }

    public void sendKlarnaMobileSDKError(@Nullable KlarnaCheckoutView view, @NonNull KlarnaMobileSDKError klarnaMobileSDKError) {
        KlarnaEventHandlerEventsUtil.sendKlarnaMobileSDKError(this, view, klarnaMobileSDKError);
    }

    public void sendOnResizedEvent(@Nullable EventDispatcher eventDispatcher, int viewId, int height) {
        WritableMap params = ArgumentsUtil.createMap(
                new HashMap<String, Object>() {{
                    put("height", String.valueOf(height));
                }}
        );
        ComponentEvent event = new ComponentEvent(viewId, EVENT_NAME_ON_RESIZED, params);
        postEvent(eventDispatcher, event);
    }
}
