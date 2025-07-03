package com.klarna.mobile.sdk.reactnative.checkout;

import android.os.Handler;
import android.os.Looper;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContext;
import com.facebook.react.bridge.ReadableArray;
import com.facebook.react.uimanager.ThemedReactContext;
import com.facebook.react.uimanager.UIManagerHelper;
import com.facebook.react.uimanager.annotations.ReactProp;
import com.facebook.react.uimanager.events.EventDispatcher;
import com.klarna.mobile.sdk.api.checkout.KlarnaCheckoutView;
import com.klarna.mobile.sdk.reactnative.common.WebViewResizeObserver;
import com.klarna.mobile.sdk.reactnative.common.ui.ResizeObserverWrapperView;
import com.klarna.mobile.sdk.reactnative.spec.RNKlarnaCheckoutViewSpec;

import java.util.Map;
import java.util.Objects;
import java.util.WeakHashMap;

public class KlarnaCheckoutViewManager extends RNKlarnaCheckoutViewSpec<ResizeObserverWrapperView<KlarnaCheckoutView>> {

    private static final int MAX_SET_SNIPPET_RETRIES = 3;
    public static final String KLARNA_CHECKOUT_VIEW_REACT_CLASS = "RNKlarnaCheckoutView";
    // Commands that can be triggered from RN
    public static final String COMMAND_SET_SNIPPET = "setSnippet";
    public static final String COMMAND_SUSPEND = "suspend";
    public static final String COMMAND_RESUME = "resume";

    private final ReactApplicationContext reactAppContext;
    /**
     * Store a map of views to event dispatchers so we send up events via the right views.
     */
    private final Map<ResizeObserverWrapperView<KlarnaCheckoutView>, EventDispatcher> viewToDispatcher;
    private final Map<KlarnaCheckoutView, ResizeObserverWrapperView<KlarnaCheckoutView>> checkoutViewToResizeObserverWrapperMap = new WeakHashMap<>();
    private final Map<ResizeObserverWrapperView<KlarnaCheckoutView>, Integer> setSnippetRetriesMap = new WeakHashMap<>();
    private final Map<ResizeObserverWrapperView<KlarnaCheckoutView>, String> snippetMap = new WeakHashMap<>();

    private final KlarnaCheckoutViewEventSender eventSender;
    private final KlarnaCheckoutViewEventHandler eventHandler;
    private final Handler handler = new Handler(Looper.getMainLooper());
    private Runnable javascriptInterfaceInjectionRunnable;

    public KlarnaCheckoutViewManager(ReactApplicationContext reactApplicationContext) {
        super();
        viewToDispatcher = new WeakHashMap<>();
        reactAppContext = reactApplicationContext;
        eventSender = new KlarnaCheckoutViewEventSender(viewToDispatcher);
        eventHandler = new KlarnaCheckoutViewEventHandler(eventSender, klarnaCheckoutView -> {
            ResizeObserverWrapperView<KlarnaCheckoutView> resizeObserverWrapperView =
                    checkoutViewToResizeObserverWrapperMap.get(klarnaCheckoutView);
            if (resizeObserverWrapperView == null) {
                return;
            }
            Integer retries = setSnippetRetriesMap.getOrDefault(resizeObserverWrapperView, 0);
            if (retries == null || retries >= MAX_SET_SNIPPET_RETRIES) {
                return;
            }
            String snippet = snippetMap.get(resizeObserverWrapperView);
            handler.postDelayed(() -> {
                if (klarnaCheckoutView != null && klarnaCheckoutView.getHeight() == 0) {
                    setSnippet(resizeObserverWrapperView, snippet);
                    setSnippetRetriesMap.put(resizeObserverWrapperView, retries + 1);
                }
            }, 500);
        });
    }

    @NonNull
    @Override
    public String getName() {
        return KLARNA_CHECKOUT_VIEW_REACT_CLASS;
    }

    @NonNull
    @Override
    protected ResizeObserverWrapperView<KlarnaCheckoutView> createViewInstance(@NonNull ThemedReactContext themedReactContext) {
        KlarnaCheckoutView klarnaCheckoutView = new KlarnaCheckoutView(reactAppContext.getCurrentActivity(), null, 0, eventHandler);
        ResizeObserverWrapperView<KlarnaCheckoutView> view = new ResizeObserverWrapperView<>(reactAppContext, null, klarnaCheckoutView);

        checkoutViewToResizeObserverWrapperMap.put(klarnaCheckoutView, view);

        // Each view has its own event dispatcher.
        EventDispatcher eventDispatcher = UIManagerHelper.getEventDispatcherForReactTag((ReactContext) view.getContext(), view.getId());
        viewToDispatcher.put(view, eventDispatcher);

        view.initiateWebViewResizeObserver(WebViewResizeObserver.TargetElement.CHECKOUT_CONTAINER, (resizeObserverWrapperView, newHeight) -> {
            if (resizeObserverWrapperView == null || eventDispatcher == null) {
                return;
            }
            eventSender.sendOnResizedEvent(resizeObserverWrapperView.getView(), newHeight);
        });
        view.addJavascriptInterfaceToWebView();

        view.addOnAttachStateChangeListener(new View.OnAttachStateChangeListener() {
            @Override
            public void onViewAttachedToWindow(@NonNull View v) {
                eventSender.sendOnKlarnaCheckoutViewReadyEvent(view.getView());
                v.removeOnAttachStateChangeListener(this);
            }

            @Override
            public void onViewDetachedFromWindow(@NonNull View v) {
            }
        });

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
    public void receiveCommand(@NonNull ResizeObserverWrapperView<KlarnaCheckoutView> root, String commandId, @Nullable ReadableArray args) {
        switch (commandId) {
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

    @ReactProp(name = "returnUrl")
    @Override
    public void setReturnUrl(ResizeObserverWrapperView<KlarnaCheckoutView> view, @Nullable String value) {
        KlarnaCheckoutView checkoutView = view.getView();
        if (checkoutView != null) {
            if (!Objects.equals(value, checkoutView.getReturnURL())) {
                checkoutView.setReturnURL(value);
            }
        }
    }

    @Override
    public void setSnippet(ResizeObserverWrapperView<KlarnaCheckoutView> view, String snippet) {
        if (view == null) {
            return;
        }

        KlarnaCheckoutView klarnaCheckoutView = view.getView();
        if (klarnaCheckoutView == null) {
            return;
        }

        if (snippet == null || snippet.isEmpty()) {
            return;
        }

        snippetMap.put(view, snippet);

        klarnaCheckoutView.setSnippet(snippet);
        view.addJavascriptInterfaceToWebView();

        if (javascriptInterfaceInjectionRunnable != null) {
            handler.removeCallbacks(javascriptInterfaceInjectionRunnable);
        }
        javascriptInterfaceInjectionRunnable = view::injectListenerToWebView;
        handler.postDelayed(javascriptInterfaceInjectionRunnable, 500);
    }

    @Override
    public void suspend(ResizeObserverWrapperView<KlarnaCheckoutView> view) {
        KlarnaCheckoutView klarnaCheckoutView = view.getView();
        if (klarnaCheckoutView != null) {
            klarnaCheckoutView.suspend();
        }
    }

    @Override
    public void resume(ResizeObserverWrapperView<KlarnaCheckoutView> view) {
        KlarnaCheckoutView klarnaCheckoutView = view.getView();
        if (klarnaCheckoutView != null) {
            klarnaCheckoutView.resume();
        }
    }
}
