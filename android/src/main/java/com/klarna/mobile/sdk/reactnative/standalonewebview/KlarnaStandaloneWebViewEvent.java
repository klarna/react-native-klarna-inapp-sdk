package com.klarna.mobile.sdk.reactnative.standalonewebview;

import androidx.annotation.IdRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.facebook.react.bridge.Arguments;
import com.facebook.react.bridge.WritableMap;
import com.facebook.react.uimanager.events.Event;

/**
 * A `KlarnaStandaloneWebViewEvent` builds the event that will be eventually sent to the React Native side.
 * <p>
 * It consists of a flat JSON object with a single `name` parameter for the event name as well as
 * other, optional parameters depending on the function that was called.
 */
public class KlarnaStandaloneWebViewEvent extends Event<KlarnaStandaloneWebViewEvent> {

    // List of possible events that can be sent to the React Native side.
    public enum Event {
        // This event is sent just before loading a URL
        ON_LOAD_START("onLoadStart"),
        // This event is sent when loading a URL is done
        ON_LOAD("onLoad"),
        // This event is sent when loading a URL encounters an error
        ON_ERROR("onError"),
        // This event is sent when the progress of loading a page changes
        ON_LOAD_PROGRESS("onLoadProgress"),
        // This event is sent when the Mobile SDK wants to send an event to the Web View
        ON_KLARNA_MESSAGE("onKlarnaMessage"),
        // This event is sent when the WebView's render process has exited
        ON_RENDER_PROCESS_GONE("onRenderProcessGone");

        public final String name;

        Event(String name) {
            this.name = name;
        }
    }

    @NonNull
    private final String eventName;

    @Nullable
    private final WritableMap additionalParams;

    public KlarnaStandaloneWebViewEvent(@IdRes int viewId, @NonNull String eventName, @Nullable WritableMap additionalParams) {
        super(viewId);
        this.eventName = eventName;
        this.additionalParams = additionalParams;
    }

    @Override
    public String getEventName() {
        return eventName;
    }

    @Nullable
    @Override
    protected WritableMap getEventData() {
        if (additionalParams != null) {
            return additionalParams;
        }
        return Arguments.createMap();
    }

}
