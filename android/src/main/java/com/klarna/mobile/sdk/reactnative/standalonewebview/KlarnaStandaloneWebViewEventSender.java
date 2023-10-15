package com.klarna.mobile.sdk.reactnative.standalonewebview;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.facebook.react.bridge.Arguments;
import com.facebook.react.bridge.ReactContext;
import com.facebook.react.bridge.ReadableMap;
import com.facebook.react.bridge.WritableMap;
import com.facebook.react.uimanager.UIManagerHelper;
import com.facebook.react.uimanager.events.EventDispatcher;
import com.klarna.mobile.sdk.api.KlarnaProductEvent;
import com.klarna.mobile.sdk.api.standalonewebview.KlarnaStandaloneWebView;
import com.klarna.mobile.sdk.reactnative.common.ArgumentsUtil;

import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.Map;

// TODO: Double-check that we're sending the correct values for parameters of the events.

/**
 * This class is responsible for sending some KlarnaStandaloneWebView-related events to the React Native side.
 */
public class KlarnaStandaloneWebViewEventSender {

    private static final String PARAM_NAME_NAVIGATION_EVENT = "navigationEvent";
    private static final String PARAM_NAME_NAVIGATION_ERROR = "navigationError";
    private static final String PARAM_NAME_PROGRESS_EVENT = "progressEvent";
    private static final String PARAM_NAME_KLARNA_MESSAGE_EVENT = "klarnaMessageEvent";
    private static final String PARAM_NAME_ERROR_MESSAGE = "errorMessage";
    private static final String PARAM_NAME_EVENT = "event";
    private static final String PARAM_NAME_NEW_URL = "newUrl";
    private static final String PARAM_NAME_TITLE = "title";
    private static final String PARAM_NAME_URL = "url";
    private static final String PARAM_NAME_PROGRESS = "progress";
    private static final String PARAM_NAME_IS_LOADING = "isLoading";
    private static final String PARAM_NAME_WEB_VIEW_STATE = "webViewState";
    private static final String PARAM_NAME_ACTION = "action";

    private final Map<WeakReference<KlarnaStandaloneWebView>, EventDispatcher> viewToDispatcher;

    KlarnaStandaloneWebViewEventSender(@NonNull final Map<WeakReference<KlarnaStandaloneWebView>, EventDispatcher> viewToDispatcher) {
        this.viewToDispatcher = viewToDispatcher;
    }

    public void sendProgressChangeEvent(@Nullable KlarnaStandaloneWebView view, int progress) {
        ReadableMap webViewStateMap = (view == null) ? Arguments.createMap() : buildWebViewStateMap(view.getUrl(), view.getTitle(), progress);
        WritableMap params = ArgumentsUtil.createMap(new HashMap<>() {{
            put(PARAM_NAME_PROGRESS_EVENT, webViewStateMap);
        }});
        postEventForView(KlarnaStandaloneWebViewEvent.Event.ON_PROGRESS_CHANGE, params, view);
    }

    public void sendLoadErrorEvent(@Nullable KlarnaStandaloneWebView view, String description) {
        ReadableMap navigationErrorMap = ArgumentsUtil.createMap(new HashMap<>() {{
            put(PARAM_NAME_ERROR_MESSAGE, description);
        }});
        WritableMap params = ArgumentsUtil.createMap(new HashMap<>() {{
            put(PARAM_NAME_NAVIGATION_ERROR, navigationErrorMap);
        }});
        postEventForView(KlarnaStandaloneWebViewEvent.Event.ON_LOAD_ERROR, params, view);
    }

    public void sendNavigationEvent(KlarnaStandaloneWebView view, KlarnaStandaloneWebViewEvent.Event event) {
        WritableMap params = ArgumentsUtil.createMap(new HashMap<>() {{
            put(PARAM_NAME_NAVIGATION_EVENT, buildNavigationEventMap(view, event));
        }});
        postEventForView(event, params, view);
    }

    public void sendKlarnaMessageEvent(@NonNull KlarnaStandaloneWebView view, @NonNull KlarnaProductEvent klarnaProductEvent) {
        WritableMap params = ArgumentsUtil.createMap(new HashMap<>() {{
            put(PARAM_NAME_KLARNA_MESSAGE_EVENT, buildKlarnaMessageEventMap(klarnaProductEvent));
        }});
        postEventForView(KlarnaStandaloneWebViewEvent.Event.ON_KLARNA_MESSAGE, params, view);
    }

    /**
     * Creates an event from event name and a map of params. Sends it via the right dispatcher.
     *
     * @param event            name of the event should match getExportedCustomDirectEventTypeConstants
     * @param additionalParams payload of the event
     * @param view             source native view
     */
    private void postEventForView(KlarnaStandaloneWebViewEvent.Event event, WritableMap additionalParams, KlarnaStandaloneWebView view) {
        KlarnaStandaloneWebView klarnaStandaloneWebView = getKlarnaStandaloneWebView(view);
        if (klarnaStandaloneWebView != null) {
            EventDispatcher eventDispatcher = UIManagerHelper.getEventDispatcherForReactTag((ReactContext) klarnaStandaloneWebView.getContext(), klarnaStandaloneWebView.getId());
            if (eventDispatcher != null) {
                KlarnaStandaloneWebViewEvent e = new KlarnaStandaloneWebViewEvent(klarnaStandaloneWebView.getId(), event.name, additionalParams);
                eventDispatcher.dispatchEvent(e);
            }
        }
    }

    @Nullable
    private KlarnaStandaloneWebView getKlarnaStandaloneWebView(KlarnaStandaloneWebView klarnaStandaloneWebView) {
        for (WeakReference<KlarnaStandaloneWebView> reference : viewToDispatcher.keySet()) {
            KlarnaStandaloneWebView webView = reference.get();
            if (webView != null && webView == klarnaStandaloneWebView) {
                return webView;
            }
        }
        return null;
    }

    private ReadableMap buildWebViewStateMap(String url, String title, int progress) {
        return ArgumentsUtil.createMap(new HashMap<>() {{
            put(PARAM_NAME_URL, url);
            put(PARAM_NAME_TITLE, title);
            put(PARAM_NAME_PROGRESS, String.valueOf(progress));
            put(PARAM_NAME_IS_LOADING, progress < 100);
        }});
    }

    private ReadableMap buildNavigationEventMap(@Nullable KlarnaStandaloneWebView view, KlarnaStandaloneWebViewEvent.Event event) {
        if (view == null) {
            return Arguments.createMap();
        } else {
            return ArgumentsUtil.createMap(new HashMap<>() {{
                // Possible values for 'event' are 'willLoad', 'loadStarted', and 'loadEnded'
                put(PARAM_NAME_EVENT, event == KlarnaStandaloneWebViewEvent.Event.ON_BEFORE_LOAD ? "loadStarted" : "loadEnded");
                put(PARAM_NAME_NEW_URL, view.getUrl());
                put(PARAM_NAME_WEB_VIEW_STATE, buildWebViewStateMap(view.getUrl(), view.getTitle(), view.getProgress()));
            }});
        }
    }

    private ReadableMap buildKlarnaMessageEventMap(KlarnaProductEvent klarnaProductEvent) {
        return ArgumentsUtil.createMap(new HashMap<>() {{
            put(PARAM_NAME_ACTION, klarnaProductEvent.getAction());
        }});
    }

}
