package com.klarna.mobile.sdk.reactnative.checkout;

import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContext;
import com.facebook.react.bridge.ReadableArray;
import com.facebook.react.bridge.ReadableMap;
import com.facebook.react.uimanager.ThemedReactContext;
import com.facebook.react.uimanager.UIManagerHelper;
import com.facebook.react.uimanager.events.EventDispatcher;
import com.klarna.mobile.sdk.api.KlarnaLoggingLevel;
import com.klarna.mobile.sdk.api.checkout.KlarnaCheckoutView;
import com.klarna.mobile.sdk.reactnative.common.WebViewResizeObserver;
import com.klarna.mobile.sdk.reactnative.common.ui.WrapperView;
import com.klarna.mobile.sdk.reactnative.spec.RNKlarnaCheckoutViewSpec;

import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.Map;

public class KlarnaCheckoutViewManager extends RNKlarnaCheckoutViewSpec<WrapperView<KlarnaCheckoutView>> {

    public static final String KLARNA_CHECKOUT_VIEW_REACT_CLASS = "RNKlarnaCheckoutView";

    // Commands that can be triggered from RN
    public static final String COMMAND_SET_RETURN_URL = "setReturnUrl";
    public static final String COMMAND_SET_CHECKOUT_OPTIONS = "setCheckoutOptions";
    public static final String COMMAND_SET_SNIPPET = "setSnippet";
    public static final String COMMAND_SUSPEND = "suspend";
    public static final String COMMAND_RESUME = "resume";

    private final ReactApplicationContext reactAppContext;

    /**
     * Store a map of views to event dispatchers so we send up events via the right views.
     */
    private final Map<WeakReference<WrapperView<KlarnaCheckoutView>>, EventDispatcher> viewToDispatcher;
    /**
     * Store a map of views to resize observers.
     */
    private final Map<WrapperView<KlarnaCheckoutView>, WebViewResizeObserver> viewToResizeObserver;

    private final KlarnaCheckoutViewEventSender eventSender;
    private final KlarnaCheckoutViewEventHandler eventHandler;

    public KlarnaCheckoutViewManager(ReactApplicationContext reactApplicationContext) {
        super();
        this.viewToDispatcher = new HashMap<>();
        this.viewToResizeObserver = new HashMap<>();
        this.reactAppContext = reactApplicationContext;
        this.eventSender = new KlarnaCheckoutViewEventSender(viewToDispatcher);
        this.eventHandler = new KlarnaCheckoutViewEventHandler(eventSender);
    }

    @NonNull
    @Override
    public String getName() {
        return KLARNA_CHECKOUT_VIEW_REACT_CLASS;
    }

    @NonNull
    @Override
    protected WrapperView<KlarnaCheckoutView> createViewInstance(@NonNull ThemedReactContext themedReactContext) {
        KlarnaCheckoutView klarnaCheckoutView = new KlarnaCheckoutView(reactAppContext, null, 0, eventHandler);
        klarnaCheckoutView.setLoggingLevel(KlarnaLoggingLevel.Verbose);
        WrapperView<KlarnaCheckoutView> view = new WrapperView<>(reactAppContext, null, klarnaCheckoutView);

        initializeWebViewResizeObserver(view);
        addResizeInterfaceToView(view);

        // Each view has its own event dispatcher.
        EventDispatcher dispatcher = UIManagerHelper.getEventDispatcherForReactTag((ReactContext) view.getContext(), view.getId());
        viewToDispatcher.put(new WeakReference<>(view), dispatcher);

        return view;
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
        return eventSender.getExportedCustomDirectEventTypeConstants();
    }

    /**
     * Handles commands received from RN to a specific view.
     *
     * @param root      view receiving the command
     * @param commandId identifier of the command
     * @param args      array of command arguments
     */
    @Override
    public void receiveCommand(@NonNull WrapperView<KlarnaCheckoutView> root, String commandId, @Nullable ReadableArray args) {
        switch (commandId) {
            case COMMAND_SET_RETURN_URL:
                setReturnUrl(root, args != null ? args.getString(0) : null);
                break;
            case COMMAND_SET_CHECKOUT_OPTIONS:
                setCheckoutOptions(root, args != null ? args.getMap(0) : null);
                break;
            case COMMAND_SET_SNIPPET:
                setSnippet(root, args != null ? args.getString(0) : null);
                break;
            case COMMAND_SUSPEND:
                suspend(root);
                break;
            case COMMAND_RESUME:
                resume(root);
                break;
        }
    }

    @Override
    public void setReturnUrl(WrapperView<KlarnaCheckoutView> view, @Nullable String value) {
        view.getView().setReturnURL(value);
    }

    @Override
    public void setCheckoutOptions(WrapperView<KlarnaCheckoutView> view, @Nullable ReadableMap value) {
        if (value == null || view.getView().getCheckoutOptions() != null) {
            return;
        }
        if (value.hasKey("merchantHandlesEPM")) {
            view.getView().getCheckoutOptions().setMerchantHandlesEPM(value.getBoolean("merchantHandlesEPM"));
        }
        if (value.hasKey("merchantHandlesValidationErrors")) {
            view.getView().getCheckoutOptions().setMerchantHandlesValidationErrors(value.getBoolean("merchantHandlesValidationErrors"));
        }
    }

    @Override
    public void setSnippet(WrapperView<KlarnaCheckoutView> view, String snippet) {
        view.getView().setSnippet(snippet);
        addResizeInterfaceToView(view);
        // Delay the injection of the resize observer to ensure the web view is ready
        final Handler handler = new Handler();
        handler.postDelayed(() -> {
            injectResizeObserverToView(view);
        }, 500);
    }

    @Override
    public void suspend(WrapperView<KlarnaCheckoutView> view) {
        view.getView().suspend();
    }

    @Override
    public void resume(WrapperView<KlarnaCheckoutView> view) {
        view.getView().resume();
    }

    private void initializeWebViewResizeObserver(WrapperView<KlarnaCheckoutView> view) {
        WebViewResizeObserver observerFromMap = viewToResizeObserver.get(view);
        if (observerFromMap != null) {
            // observer already set
            return;
        }
        EventDispatcher eventDispatcher = UIManagerHelper.getEventDispatcherForReactTag((ReactContext) view.getContext(), view.getId());
        WebViewResizeObserver webViewResizeObserver = createWebViewResizeObserver(eventDispatcher, view);
        viewToResizeObserver.put(view, webViewResizeObserver);
        Log.d("RNCheckout", "initializeWebViewResizeObserver: observer initialized");
    }

    @NonNull
    private WebViewResizeObserver createWebViewResizeObserver(EventDispatcher eventDispatcher, WrapperView<KlarnaCheckoutView> view) {
        WebViewResizeObserver.WebViewResizeObserverCallback callback = new WebViewResizeObserver.WebViewResizeObserverCallback() {
            private WeakReference<WrapperView<KlarnaCheckoutView>> viewRef = new WeakReference<>(view);

            @Override
            public void onResized(int value) {
                Log.d("RNCheckout", "onResized: " + value);
                WrapperView<KlarnaCheckoutView> view = viewRef.get();
                if (view == null) {
                    Log.e("RNCheckout", "onResized: view not found");
                    return;
                }
                eventSender.sendOnResizedEvent(eventDispatcher, view.getId(), value);
            }
        };
        return new WebViewResizeObserver(
                callback,
                WebViewResizeObserver.TargetElement.CHECKOUT_CONTAINER
        );
    }

    private void addResizeInterfaceToView(WrapperView<KlarnaCheckoutView> view) {
        WebViewResizeObserver observerFromMap = viewToResizeObserver.get(view);
        if (observerFromMap == null) {
            Log.e("RNCheckout", "addResizeInterfaceToView: observer not set");
            // observer not set
            return;
        }
        WebView webView = getCheckoutViewWebView(view.getView());
        if (webView == null) {
            Log.e("RNCheckout", "addResizeInterfaceToView: web view not found");
            return;
        }
        observerFromMap.addInterface(webView);
        Log.d("RNCheckout", "addResizeInterfaceToView: interface added");
    }

    private void injectResizeObserverToView(WrapperView<KlarnaCheckoutView> view) {
        WebViewResizeObserver observerFromMap = viewToResizeObserver.get(view);
        if (observerFromMap == null) {
            Log.e("RNCheckout", "injectResizeObserverToView: observer not set");
            // observer not set
            return;
        }
        WebView webView = getCheckoutViewWebView(view.getView());
        if (webView == null) {
            Log.e("RNCheckout", "injectResizeObserverToView: web view not found");
            return;
        }
        observerFromMap.injectListener(webView);
        Log.d("RNCheckout", "injectResizeObserverToView: observer injected");
    }

    /**
     * Attempts to retrieve the web view from within the KlarnaPaymentView. This is really fragile.
     */
    private WebView getCheckoutViewWebView(KlarnaCheckoutView view) {
        if (view == null) {
            return null;
        }
        for (int i = 0; i < view.getChildCount(); i++) {
            View child = view.getChildAt(i);

            if (child instanceof WebView) {
                return (WebView) child;
            }
        }
        return null;
    }
}
