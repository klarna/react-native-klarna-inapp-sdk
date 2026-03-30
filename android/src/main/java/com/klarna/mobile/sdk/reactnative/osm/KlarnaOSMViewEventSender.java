package com.klarna.mobile.sdk.reactnative.osm;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.facebook.react.bridge.WritableMap;
import com.facebook.react.uimanager.events.EventDispatcher;
import com.klarna.mobile.sdk.KlarnaMobileSDKError;
import com.klarna.mobile.sdk.api.osm.KlarnaOSMView;
import com.klarna.mobile.sdk.reactnative.common.event.ComponentEventSender;
import com.klarna.mobile.sdk.reactnative.common.ui.WrapperView;
import com.klarna.mobile.sdk.reactnative.common.util.ArgumentsUtil;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class KlarnaOSMViewEventSender extends ComponentEventSender<WrapperView<KlarnaOSMView>> {

    private static final String EVENT_NAME_ON_ERROR = "onError";
    private static final String EVENT_NAME_ON_RESIZED = "onResized";
    private static final String EVENT_NAME_ON_OSM_VIEW_READY = "onOSMViewReady";

    KlarnaOSMViewEventSender(@NonNull final Map<WrapperView<KlarnaOSMView>, EventDispatcher> viewToDispatcher) {
        super(viewToDispatcher);
    }

    @Override
    public Collection<String> getCallbackEventNames() {
        return Arrays.asList(
                EVENT_NAME_ON_ERROR,
                EVENT_NAME_ON_RESIZED,
                EVENT_NAME_ON_OSM_VIEW_READY
        );
    }

    public void sendKlarnaMobileSDKError(@Nullable KlarnaOSMView view, @NonNull KlarnaMobileSDKError klarnaMobileSDKError) {
        sendError(view, klarnaMobileSDKError.getName(), klarnaMobileSDKError.getMessage(), klarnaMobileSDKError.isFatal());
    }

    public void sendError(@Nullable KlarnaOSMView view, @NonNull String name, @NonNull String message, boolean isFatal) {
        WritableMap errorMap = ArgumentsUtil.createMap(new HashMap<String, Object>() {{
            put("name", name);
            put("message", message);
            put("isFatal", isFatal);
        }});
        WritableMap params = ArgumentsUtil.createMap(new HashMap<String, Object>() {{
            put("error", errorMap);
        }});
        postEventForView(view, EVENT_NAME_ON_ERROR, params);
    }

    public void sendOnResizedEvent(@Nullable KlarnaOSMView view, int height) {
        WritableMap params = ArgumentsUtil.createMap(new HashMap<String, Object>() {{
            put("height", String.valueOf(height));
        }});
        postEventForView(view, EVENT_NAME_ON_RESIZED, params);
    }

    public void sendOnOSMViewReadyEvent(@Nullable KlarnaOSMView view) {
        postEventForView(view, EVENT_NAME_ON_OSM_VIEW_READY, null);
    }
}
