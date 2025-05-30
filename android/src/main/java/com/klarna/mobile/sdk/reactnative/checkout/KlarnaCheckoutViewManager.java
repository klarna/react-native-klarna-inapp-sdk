package com.klarna.mobile.sdk.reactnative.checkout;

import android.os.Handler;
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
import com.klarna.mobile.sdk.api.KlarnaLoggingLevel;
import com.klarna.mobile.sdk.api.checkout.KlarnaCheckoutView;
import com.klarna.mobile.sdk.reactnative.common.WebViewResizeObserver;
import com.klarna.mobile.sdk.reactnative.common.ui.ResizeObserverWrapperView;
import com.klarna.mobile.sdk.reactnative.spec.RNKlarnaCheckoutViewSpec;

import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class KlarnaCheckoutViewManager extends RNKlarnaCheckoutViewSpec<ResizeObserverWrapperView<KlarnaCheckoutView>> {

    public static final String KLARNA_CHECKOUT_VIEW_REACT_CLASS = "RNKlarnaCheckoutView";

    // Commands that can be triggered from RN
    public static final String COMMAND_SET_SNIPPET = "setSnippet";
    public static final String COMMAND_SUSPEND = "suspend";
    public static final String COMMAND_RESUME = "resume";

    private final ReactApplicationContext reactAppContext;

    /**
     * Store a map of views to event dispatchers so we send up events via the right views.
     */
    private final Map<WeakReference<ResizeObserverWrapperView<KlarnaCheckoutView>>, EventDispatcher> viewToDispatcher;

    private final KlarnaCheckoutViewEventSender eventSender;
    private final KlarnaCheckoutViewEventHandler eventHandler;

    public KlarnaCheckoutViewManager(ReactApplicationContext reactApplicationContext) {
        super();
        viewToDispatcher = new HashMap<>();
        reactAppContext = reactApplicationContext;
        eventSender = new KlarnaCheckoutViewEventSender(viewToDispatcher);
        eventHandler = new KlarnaCheckoutViewEventHandler(eventSender);
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
        klarnaCheckoutView.setLoggingLevel(KlarnaLoggingLevel.Verbose);
        ResizeObserverWrapperView<KlarnaCheckoutView> view = new ResizeObserverWrapperView<>(reactAppContext, null, klarnaCheckoutView);

        // Each view has its own event dispatcher.
        EventDispatcher eventDispatcher = UIManagerHelper.getEventDispatcherForReactTag((ReactContext) view.getContext(), view.getId());
        viewToDispatcher.put(new WeakReference<>(view), eventDispatcher);

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
        if (snippet != null && !snippet.isEmpty()) {
            view.getView().setSnippet(snippet);
            view.addJavascriptInterfaceToWebView();
            // Delay the injection of the resize observer to ensure the web view is ready
            final Handler handler = new Handler();
            handler.postDelayed(view::injectListenerToWebView, 500);
        }
    }

    @Override
    public void suspend(ResizeObserverWrapperView<KlarnaCheckoutView> view) {
        view.getView().suspend();
    }

    @Override
    public void resume(ResizeObserverWrapperView<KlarnaCheckoutView> view) {
        view.getView().resume();
    }
}
