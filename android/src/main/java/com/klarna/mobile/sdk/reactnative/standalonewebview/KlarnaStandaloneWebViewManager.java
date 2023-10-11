package com.klarna.mobile.sdk.reactnative.standalonewebview;

import android.app.Application;
import android.graphics.Bitmap;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.facebook.react.bridge.Arguments;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContext;
import com.facebook.react.bridge.ReadableArray;
import com.facebook.react.bridge.ReadableMap;
import com.facebook.react.bridge.WritableMap;
import com.facebook.react.common.MapBuilder;
import com.facebook.react.uimanager.ThemedReactContext;
import com.facebook.react.uimanager.UIManagerHelper;
import com.facebook.react.uimanager.annotations.ReactProp;
import com.facebook.react.uimanager.events.EventDispatcher;
import com.klarna.mobile.sdk.api.standalonewebview.KlarnaStandaloneWebView;
import com.klarna.mobile.sdk.api.standalonewebview.KlarnaStandaloneWebViewClient;
import com.klarna.mobile.sdk.reactnative.common.ArgumentsUtil;
import com.klarna.mobile.sdk.reactnative.spec.RNKlarnaStandaloneWebViewSpec;

import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class KlarnaStandaloneWebViewManager extends RNKlarnaStandaloneWebViewSpec<KlarnaStandaloneWebView> {

    // Commands that can be triggered from RN
    public static final String COMMAND_LOAD = "load";
    public static final String COMMAND_GO_FORWARD = "goForward";
    public static final String COMMAND_GO_BACK = "goBack";
    public static final String COMMAND_RELOAD = "reload";

    private static final String REACT_CLASS = "RNKlarnaStandaloneWebView";

    // Store a list of views to event dispatchers so we send up events via the right views.
    private final Map<WeakReference<KlarnaStandaloneWebView>, EventDispatcher> viewToDispatcher;
    private final KlarnaStandaloneWebViewClient klarnaStandaloneWebViewClient = new KlarnaStandaloneWebViewClient() {
        @Override
        public void onPageStarted(@Nullable KlarnaStandaloneWebView view, @Nullable String url, @Nullable Bitmap favicon) {
            WritableMap params = ArgumentsUtil.createMap(
                    new HashMap<>() {{
                        put("navigationEvent", buildNavigationEventMap(view, KlarnaStandaloneWebViewEvent.Event.ON_BEFORE_LOAD));
                    }}
            );
            postEventForView(KlarnaStandaloneWebViewEvent.Event.ON_BEFORE_LOAD, params, view);
        }

        @Override
        public void onPageFinished(@Nullable KlarnaStandaloneWebView view, @Nullable String url) {
            // FIXME: Sometimes this method get called more than once per page!
            WritableMap params = ArgumentsUtil.createMap(
                    new HashMap<>() {{
                        put("navigationEvent", buildNavigationEventMap(view, KlarnaStandaloneWebViewEvent.Event.ON_LOAD));
                    }}
            );
            postEventForView(KlarnaStandaloneWebViewEvent.Event.ON_LOAD, params, view);
        }
    };

    public KlarnaStandaloneWebViewManager(ReactApplicationContext reactContext, Application applicationContext) {
        super();
        this.viewToDispatcher = new HashMap<>();
    }

    @ReactProp(name = "returnUrl")
    @Override
    public void setReturnUrl(KlarnaStandaloneWebView view, @Nullable String returnUrl) {
        if (!Objects.equals(returnUrl, view.getReturnURL())) {
            view.setReturnURL(returnUrl);
        }
    }

    @NonNull
    @Override
    public String getName() {
        return REACT_CLASS;
    }

    /**
     * Handles commands received from RN to a specific view.
     *
     * @param root      view receiving the command
     * @param commandId identifier of the command
     * @param args      array of command arguments
     */
    @Override
    public void receiveCommand(@NonNull KlarnaStandaloneWebView root, String commandId, @Nullable ReadableArray args) {
        switch (commandId) {
            case COMMAND_LOAD:
                if (args != null && args.size() > 0) {
                    String url = args.getString(0);
                    load(root, url);
                }
                break;
            case COMMAND_GO_FORWARD:
                goForward(root);
                break;
            case COMMAND_GO_BACK:
                goBack(root);
                break;
            case COMMAND_RELOAD:
                reload(root);
                break;
        }
    }

    @Override
    public void load(KlarnaStandaloneWebView view, String url) {
        view.loadUrl(url);
    }

    @Override
    public void goForward(KlarnaStandaloneWebView view) {
        view.goForward();
    }

    @Override
    public void goBack(KlarnaStandaloneWebView view) {
        view.goBack();
    }

    @Override
    public void reload(KlarnaStandaloneWebView view) {
        view.reload();
    }

    /**
     * Exposes direct event types that will be accessible as prop "callbacks" from RN.
     * <p>
     * Structure must follow:
     * { "<eventName>": {"registrationName": "<eventName>"} }
     */
    @Nullable
    @Override
    public Map<String, Object> getExportedCustomDirectEventTypeConstants() {
        MapBuilder.Builder<String, Object> builder = MapBuilder.builder();
        builder.put(KlarnaStandaloneWebViewEvent.Event.ON_BEFORE_LOAD.name, MapBuilder.of("registrationName", KlarnaStandaloneWebViewEvent.Event.ON_BEFORE_LOAD.name));
        builder.put(KlarnaStandaloneWebViewEvent.Event.ON_LOAD.name, MapBuilder.of("registrationName", KlarnaStandaloneWebViewEvent.Event.ON_LOAD.name));
        return builder.build();
    }

    @NonNull
    @Override
    protected KlarnaStandaloneWebView createViewInstance(@NonNull ThemedReactContext themedReactContext) {
        // TODO Are we using the correct constructor for instantiating KlarnaStandaloneWebView?
        // TODO Should we use themedReactContext for instantiating KlarnaStandaloneWebView?
        KlarnaStandaloneWebView klarnaStandaloneWebView = new KlarnaStandaloneWebView(themedReactContext);
        klarnaStandaloneWebView.setWebViewClient(klarnaStandaloneWebViewClient);
        // Each view has its own event dispatcher.
        EventDispatcher dispatcher = UIManagerHelper.getEventDispatcherForReactTag((ReactContext) klarnaStandaloneWebView.getContext(), klarnaStandaloneWebView.getId());
        viewToDispatcher.put(new WeakReference<>(klarnaStandaloneWebView), dispatcher);
        return klarnaStandaloneWebView;
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

    private ReadableMap buildNavigationEventMap(@Nullable KlarnaStandaloneWebView view, KlarnaStandaloneWebViewEvent.Event event) {
        if (view == null) {
            return Arguments.createMap();
        } else {
            return ArgumentsUtil.createMap(
                    new HashMap<>() {{
                        // TODO Verify that we're passing the correct values
                        // Possible values for 'event' are 'willLoad', 'loadStarted', and 'loadEnded'
                        put("event", event == KlarnaStandaloneWebViewEvent.Event.ON_BEFORE_LOAD ? "loadStarted" : "loadEnded");
                        put("newUrl", view.getUrl());
                        put("webViewState", buildWebViewStateMap(view, event));
                    }}
            );
        }
    }

    private ReadableMap buildWebViewStateMap(KlarnaStandaloneWebView view, KlarnaStandaloneWebViewEvent.Event event) {
        return ArgumentsUtil.createMap(
                new HashMap<>() {{
                    // TODO Verify that we're passing the correct values
                    put("url", view.getUrl());
                    put("title", view.getTitle());
                    put("progress", String.valueOf(view.getProgress()));
                    put("isLoading", event == KlarnaStandaloneWebViewEvent.Event.ON_BEFORE_LOAD);
                }}
        );
    }

}
