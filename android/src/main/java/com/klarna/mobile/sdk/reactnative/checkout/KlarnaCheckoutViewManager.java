package com.klarna.mobile.sdk.reactnative.checkout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContext;
import com.facebook.react.bridge.ReadableArray;
import com.facebook.react.bridge.ReadableMap;
import com.facebook.react.uimanager.ThemedReactContext;
import com.facebook.react.uimanager.UIManagerHelper;
import com.facebook.react.uimanager.events.EventDispatcher;
import com.klarna.mobile.sdk.api.checkout.KlarnaCheckoutView;
import com.klarna.mobile.sdk.reactnative.spec.RNKlarnaCheckoutViewSpec;

import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.Map;

public class KlarnaCheckoutViewManager extends RNKlarnaCheckoutViewSpec<KlarnaCheckoutView> {

    public static final String KLARNA_CHECKOUT_VIEW_REACT_CLASS = "RNKlarnaCheckoutView";

    // Commands that can be triggered from RN
    public static final String COMMAND_SET_RETURN_URL = "setReturnUrl";
    public static final String COMMAND_SET_CHECKOUT_OPTIONS = "setCheckoutOptions";
    public static final String COMMAND_SET_SNIPPET = "setSnippet";
    public static final String COMMAND_SUSPEND = "suspend";
    public static final String COMMAND_RESUME = "resume";

    private final ReactApplicationContext reactAppContext;

    /**
     * Store a list of views to event dispatchers so we send up events via the right views.
     */
    private final Map<WeakReference<KlarnaCheckoutView>, EventDispatcher> viewToDispatcher;

    private final KlarnaCheckoutViewEventSender eventSender;
    private final KlarnaCheckoutViewEventHandler eventHandler;

    public KlarnaCheckoutViewManager(ReactApplicationContext reactApplicationContext) {
        super();
        this.viewToDispatcher = new HashMap<>();
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
    protected KlarnaCheckoutView createViewInstance(@NonNull ThemedReactContext themedReactContext) {
        KlarnaCheckoutView view = new KlarnaCheckoutView(reactAppContext, null, 0, eventHandler);

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
    public void receiveCommand(@NonNull KlarnaCheckoutView root, String commandId, @Nullable ReadableArray args) {
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
    public void setReturnUrl(KlarnaCheckoutView view, @Nullable String value) {
        view.setReturnURL(value);
    }

    @Override
    public void setCheckoutOptions(KlarnaCheckoutView view, @Nullable ReadableMap value) {
        if (value == null || view.getCheckoutOptions() != null) {
            return;
        }
        if (value.hasKey("merchantHandlesEPM")) {
            view.getCheckoutOptions().setMerchantHandlesEPM(value.getBoolean("merchantHandlesEPM"));
        }
        if (value.hasKey("merchantHandlesValidationErrors")) {
            view.getCheckoutOptions().setMerchantHandlesValidationErrors(value.getBoolean("merchantHandlesValidationErrors"));
        }
    }

    @Override
    public void setSnippet(KlarnaCheckoutView view, String snippet) {
        view.setSnippet(snippet);
    }

    @Override
    public void suspend(KlarnaCheckoutView view) {
        view.suspend();
    }

    @Override
    public void resume(KlarnaCheckoutView view) {
        view.resume();
    }
}
