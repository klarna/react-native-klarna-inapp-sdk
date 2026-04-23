package com.klarna.mobile.sdk.reactnative.standalonewebview;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.facebook.react.bridge.ReactContext;
import com.facebook.react.bridge.ReadableMap;
import com.facebook.react.bridge.WritableMap;
import com.facebook.react.uimanager.UIManagerHelper;
import com.facebook.react.uimanager.events.EventDispatcher;
import com.klarna.mobile.sdk.api.KlarnaProductEvent;
import com.klarna.mobile.sdk.api.standalonewebview.KlarnaStandaloneWebView;
import com.klarna.mobile.sdk.reactnative.common.serializer.DynamicMapSerializer;
import com.klarna.mobile.sdk.reactnative.common.util.ArgumentsUtil;
import com.klarna.mobile.sdk.reactnative.common.util.ParserUtil;

import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.Map;

/**
 * This class is responsible for sending some KlarnaStandaloneWebView-related events to the React Native side.
 */
public class KlarnaStandaloneWebViewEventSender {

    private static final String PARAM_NAME_NAVIGATION_EVENT = "navigationEvent";
    private static final String PARAM_NAME_NAVIGATION_ERROR = "navigationError";
    private static final String PARAM_NAME_PROGRESS_EVENT = "progressEvent";
    private static final String PARAM_NAME_KLARNA_MESSAGE_EVENT = "klarnaMessageEvent";
    private static final String PARAM_NAME_RENDER_PROCESS_GONE_EVENT = "renderProcessGoneEvent";
    private static final String PARAM_NAME_CODE = "code";
    private static final String PARAM_NAME_DESCRIPTION = "description";
    private static final String PARAM_NAME_TITLE = "title";
    private static final String PARAM_NAME_URL = "url";
    private static final String PARAM_NAME_CAN_GO_BACK = "canGoBack";
    private static final String PARAM_NAME_CAN_GO_FORWARD = "canGoForward";
    private static final String PARAM_NAME_PROGRESS = "progress";
    private static final String PARAM_NAME_LOADING = "loading";
    private static final String PARAM_NAME_ACTION = "action";
    private static final String PARAM_NAME_PARAMS = "params";
    private static final String PARAM_NAME_DID_CRASH = "didCrash";

    private final WeakReference<ReactContext> reactContextWeakReference;
    private final Map<WeakReference<KlarnaStandaloneWebView>, EventDispatcher> viewToDispatcher;

    KlarnaStandaloneWebViewEventSender(@NonNull ReactContext reactContext, @NonNull final Map<WeakReference<KlarnaStandaloneWebView>, EventDispatcher> viewToDispatcher) {
        this.reactContextWeakReference = new WeakReference<>(reactContext);
        this.viewToDispatcher = viewToDispatcher;
    }

    public void sendLoadProgressEvent(@Nullable KlarnaStandaloneWebView view, int progress) {
        WritableMap webViewMap = buildWebViewMap(view, null);
        webViewMap.putDouble(PARAM_NAME_PROGRESS, progress);
        WritableMap params = ArgumentsUtil.createMap(new HashMap<String, Object>() {{
            put(PARAM_NAME_PROGRESS_EVENT, webViewMap);
        }});
        postEventForView(KlarnaStandaloneWebViewEvent.Event.ON_LOAD_PROGRESS, params, view);
    }

    public void sendNavigationErrorEvent(@Nullable KlarnaStandaloneWebView view, int code, String description) {
        WritableMap webViewMap = buildWebViewMap(view, null);
        webViewMap.putDouble(PARAM_NAME_CODE, code);
        webViewMap.putString(PARAM_NAME_DESCRIPTION, description);
        WritableMap params = ArgumentsUtil.createMap(new HashMap<String, Object>() {{
            put(PARAM_NAME_NAVIGATION_ERROR, webViewMap);
        }});
        postEventForView(KlarnaStandaloneWebViewEvent.Event.ON_ERROR, params, view);
    }

    public void sendNavigationEvent(@Nullable KlarnaStandaloneWebView view, KlarnaStandaloneWebViewEvent.Event event, String url) {
        WritableMap webViewMap = buildWebViewMap(view, url);
        WritableMap params = ArgumentsUtil.createMap(new HashMap<String, Object>() {{
            put(PARAM_NAME_NAVIGATION_EVENT, webViewMap);
        }});
        postEventForView(event, params, view);
    }

    public void sendKlarnaMessageEvent(@Nullable KlarnaStandaloneWebView view, @NonNull KlarnaProductEvent klarnaProductEvent) {
        String paramsJson = "{}";
        try {
            paramsJson = ParserUtil.INSTANCE.toJson(DynamicMapSerializer.INSTANCE, klarnaProductEvent.getParams());
        } catch (Exception ignored) {
        }
        String finalParamsJson = paramsJson;
        ReadableMap eventMap = ArgumentsUtil.createMap(new HashMap<String, Object>() {{
            put(PARAM_NAME_ACTION, klarnaProductEvent.getAction());
            put(PARAM_NAME_PARAMS, finalParamsJson);
        }});
        WritableMap params = ArgumentsUtil.createMap(new HashMap<String, Object>() {{
            put(PARAM_NAME_KLARNA_MESSAGE_EVENT, eventMap);
        }});
        postEventForView(KlarnaStandaloneWebViewEvent.Event.ON_KLARNA_MESSAGE, params, view);
    }

    public void sendRenderProcessGoneEvent(@Nullable KlarnaStandaloneWebView view, boolean didCrash) {
        ReadableMap eventMap = ArgumentsUtil.createMap(new HashMap<String, Object>() {{
            put(PARAM_NAME_DID_CRASH, didCrash);
        }});
        WritableMap params = ArgumentsUtil.createMap(new HashMap<String, Object>() {{
            put(PARAM_NAME_RENDER_PROCESS_GONE_EVENT, eventMap);
        }});
        postEventForView(KlarnaStandaloneWebViewEvent.Event.ON_RENDER_PROCESS_GONE, params, view);
    }

    /**
     * Creates an event from event name and a map of params. Sends it via the right dispatcher.
     *
     * @param event            name of the event should match getExportedCustomDirectEventTypeConstants
     * @param additionalParams payload of the event
     * @param view             source native view
     */
    private void postEventForView(KlarnaStandaloneWebViewEvent.Event event, WritableMap additionalParams, @Nullable KlarnaStandaloneWebView view) {
        KlarnaStandaloneWebView klarnaStandaloneWebView = getKlarnaStandaloneWebView(view);
        if (klarnaStandaloneWebView != null) {
            EventDispatcher eventDispatcher = UIManagerHelper.getEventDispatcherForReactTag(reactContextWeakReference.get(), klarnaStandaloneWebView.getId());
            if (eventDispatcher != null) {
                KlarnaStandaloneWebViewEvent e = new KlarnaStandaloneWebViewEvent(klarnaStandaloneWebView.getId(), event.name, additionalParams);
                eventDispatcher.dispatchEvent(e);
            }
        }
    }

    @Nullable
    private KlarnaStandaloneWebView getKlarnaStandaloneWebView(@Nullable KlarnaStandaloneWebView klarnaStandaloneWebView) {
        if (klarnaStandaloneWebView == null) {
            return null;
        }
        for (WeakReference<KlarnaStandaloneWebView> reference : viewToDispatcher.keySet()) {
            KlarnaStandaloneWebView webView = reference.get();
            if (webView != null && webView == klarnaStandaloneWebView) {
                return webView;
            }
        }
        return null;
    }

    private WritableMap buildWebViewMap(KlarnaStandaloneWebView webView, String url) {
        return ArgumentsUtil.createMap(new HashMap<String, Object>() {{
            if (webView != null) {
                put(PARAM_NAME_URL, url != null ? url : webView.getUrl());
                put(PARAM_NAME_TITLE, webView.getTitle());
                put(PARAM_NAME_LOADING, webView.getProgress() < 100);
                put(PARAM_NAME_CAN_GO_BACK, webView.canGoBack());
                put(PARAM_NAME_CAN_GO_FORWARD, webView.canGoForward());
            } else {
                put(PARAM_NAME_URL, url);
            }
        }});
    }

}
