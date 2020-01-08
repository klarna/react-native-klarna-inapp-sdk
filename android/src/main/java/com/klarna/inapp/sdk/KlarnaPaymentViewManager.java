package com.klarna.inapp.sdk;

import android.app.Application;
import android.util.Log;

import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReadableArray;
import com.facebook.react.common.MapBuilder;
import com.facebook.react.uimanager.SimpleViewManager;
import com.facebook.react.uimanager.ThemedReactContext;
import com.facebook.react.uimanager.UIManagerModule;
import com.facebook.react.uimanager.annotations.ReactProp;
import com.facebook.react.uimanager.events.EventDispatcher;
import com.klarna.mobile.sdk.api.payments.KlarnaPaymentView;
import com.klarna.mobile.sdk.api.payments.KlarnaPaymentViewCallback;
import com.klarna.mobile.sdk.api.payments.KlarnaPaymentsSDKError;

import org.jetbrains.annotations.NotNull;

import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * Wraps the Payment Views being exposed to JS.
 */
public class KlarnaPaymentViewManager extends SimpleViewManager<PaymentViewWrapper> implements KlarnaPaymentViewCallback {

    // Commands that can be triggered from RN
    public static final int COMMAND_INITIALIZE = 1;
    public static final int COMMAND_LOAD = 2;
    public static final int COMMAND_LOAD_PAYMENT_REVIEW = 3;
    public static final int COMMAND_AUTHORIZE = 4;
    public static final int COMMAND_REAUTHORIZE = 5;
    public static final int COMMAND_FINALIZE = 6;

    public static final String REACT_CLASS = "KlarnaPaymentView";

    private final ReactApplicationContext reactAppContext;

    // Store a list of views to event dispatchers so we send up events via the right views.
    private Map<WeakReference<PaymentViewWrapper>, EventDispatcher> viewToDispatcher;

    KlarnaPaymentViewManager(ReactApplicationContext reactApplicationContext, Application app) {
        super();
        this.viewToDispatcher = new HashMap<>();
        this.reactAppContext = reactApplicationContext;
    }

    @Override
    public String getName() {
        return REACT_CLASS;
    }

    @Override
    public PaymentViewWrapper createViewInstance(ThemedReactContext context) {
        PaymentViewWrapper view = new PaymentViewWrapper(reactAppContext, null);

        // Each view has its own event dispatcher.
        EventDispatcher dispatcher = context.getNativeModule(UIManagerModule.class).getEventDispatcher();
        viewToDispatcher.put(new WeakReference<>(view), dispatcher);

        return view;
    }

    /**
     * Commads are methods that RN will expose on the JS side for a view. They can be called via
     * `UIManager.dispatchViewManagerCommand` from react.
     *
     * @return a map of command names to command IDs
     */
    @Nullable
    @Override
    public Map<String, Integer> getCommandsMap() {
        return MapBuilder.of(
                "initialize", COMMAND_INITIALIZE,
                "load", COMMAND_LOAD,
                "loadPaymentReview", COMMAND_LOAD_PAYMENT_REVIEW,
                "authorize", COMMAND_AUTHORIZE,
                "reauthorize", COMMAND_REAUTHORIZE,
                "finalize", COMMAND_FINALIZE
        );
    }

    /**
     * Handles commands received from RN to a specific view.
     *
     * @param root
     * @param commandId
     * @param args
     */
    @Override
    public void receiveCommand(@Nonnull PaymentViewWrapper root, int commandId, @Nullable ReadableArray args) {
        String sessionData = null;

        switch (commandId) {
            case COMMAND_INITIALIZE:
                if (args != null) {
                    final String token = args.getString(0);
                    final String returnUrl = args.getString(1);

                    if (token != null && returnUrl != null) {
                        root.paymentView.initialize(token, returnUrl);
                    }
                }
                break;

            case COMMAND_LOAD:
                if (args != null) {
                    sessionData = args.getString(0);
                }
                root.paymentView.load(sessionData);
                break;

            case COMMAND_LOAD_PAYMENT_REVIEW:
                root.paymentView.loadPaymentReview();
                break;

            case COMMAND_AUTHORIZE:
                if (args != null) {
                    final boolean autoFinalize = args.getBoolean(0);
                    sessionData = args.getString(1);

                    root.paymentView.authorize(autoFinalize, sessionData);                
                }
                break;

            case COMMAND_REAUTHORIZE:
                if (args != null) {
                    sessionData = args.getString(0);
                }
                root.paymentView.reauthorize(sessionData);
                break;

            case COMMAND_FINALIZE:
                if (args != null) {
                    sessionData = args.getString(0);
                }
                root.paymentView.finalize(sessionData);
                break;
        }
    }

    /**
     * Exposes direct event types that will be accessible as prop "callbacks" from RN.
     *
     * Structure must follow:
     *   { "<eventName>": {"registrationName": "<eventName>"} }
     */
    @Nullable
    @Override
    public Map getExportedCustomDirectEventTypeConstants() {
        return MapBuilder.of(
                KlarnaPaymentEvent.EVENT_NAME_ON_INITIALIZE, MapBuilder.of("registrationName", KlarnaPaymentEvent.EVENT_NAME_ON_INITIALIZE),
                KlarnaPaymentEvent.EVENT_NAME_ON_LOAD, MapBuilder.of("registrationName", KlarnaPaymentEvent.EVENT_NAME_ON_LOAD),
                KlarnaPaymentEvent.EVENT_NAME_ON_LOAD_PAYMENT_REVIEW, MapBuilder.of("registrationName", KlarnaPaymentEvent.EVENT_NAME_ON_LOAD_PAYMENT_REVIEW),
                KlarnaPaymentEvent.EVENT_NAME_ON_AUTHORIZE, MapBuilder.of("registrationName", KlarnaPaymentEvent.EVENT_NAME_ON_AUTHORIZE),
                KlarnaPaymentEvent.EVENT_NAME_ON_REAUTHORIZE, MapBuilder.of("registrationName", KlarnaPaymentEvent.EVENT_NAME_ON_REAUTHORIZE),
                KlarnaPaymentEvent.EVENT_NAME_ON_FINALIZE, MapBuilder.of("registrationName", KlarnaPaymentEvent.EVENT_NAME_ON_FINALIZE)
        );
    }

    /**
     * Payment View's payment method category (e.g: "pay_later", "pay_over_time").
     *
     * @param view
     * @param category
     */
    @ReactProp(name = "category")
    public void setCategory(PaymentViewWrapper view, String category) {
        view.paymentView.registerPaymentViewCallback(this);
        view.paymentView.setCategory(category);
    }

    /**
     * Creates an event from event name and a map of params. Sends it via the right dispatcher.
     * @param eventName
     * @param additionalParams
     * @param view
     */
    private void postEventForView(String eventName, Map additionalParams, KlarnaPaymentView view) {
        PaymentViewWrapper wrapper = wrapperForPaymentView(view);
        EventDispatcher foundDispatcher = null;
        for (WeakReference<PaymentViewWrapper> wrapperRef : viewToDispatcher.keySet()) {
            if (wrapperRef.get() == wrapper) {
                foundDispatcher = viewToDispatcher.get(wrapperRef);
            }
        }

        KlarnaPaymentEvent event = new KlarnaPaymentEvent(wrapper.getId(), eventName, additionalParams);
        foundDispatcher.dispatchEvent(event);
    }


    /* -- KlarnaPaymentView callback methods -- */

    @Override
    public void onInitialized(@NotNull KlarnaPaymentView paymentView) {
        postEventForView(KlarnaPaymentEvent.EVENT_NAME_ON_INITIALIZE, null, paymentView);
    }

    @Override
    public void onLoaded(@NotNull KlarnaPaymentView paymentView) {
        postEventForView(KlarnaPaymentEvent.EVENT_NAME_ON_LOAD, null, paymentView);
    }

    @Override
    public void onLoadPaymentReview(@NotNull KlarnaPaymentView paymentView, boolean b) {
        postEventForView(KlarnaPaymentEvent.EVENT_NAME_ON_LOAD_PAYMENT_REVIEW, null, paymentView);
    }

    @Override
    public void onAuthorized(@NotNull KlarnaPaymentView paymentView, boolean approved, @org.jetbrains.annotations.Nullable String authToken, @org.jetbrains.annotations.Nullable Boolean finalizeRequired) {
        postEventForView(KlarnaPaymentEvent.EVENT_NAME_ON_AUTHORIZE,
                MapBuilder.<String, Object>of(
                        "approved", approved,
                        "authToken", authToken,
                        "finalizeRequired", finalizeRequired),
                paymentView);
    }

    @Override
    public void onReauthorized(@NotNull KlarnaPaymentView paymentView, boolean approved, @org.jetbrains.annotations.Nullable String authToken) {
        postEventForView(KlarnaPaymentEvent.EVENT_NAME_ON_REAUTHORIZE,
                MapBuilder.<String, Object>of(
                        "approved", approved,
                        "authToken", authToken),
                paymentView);
    }

    @Override
    public void onFinalized(@NotNull KlarnaPaymentView paymentView, boolean approved, @org.jetbrains.annotations.Nullable String authToken) {
        postEventForView(KlarnaPaymentEvent.EVENT_NAME_ON_FINALIZE,
                MapBuilder.<String, Object>of(
                        "approved", approved,
                        "authToken", authToken),
                paymentView);
    }

    @Override
    public void onErrorOccurred(@NotNull KlarnaPaymentView klarnaPaymentView, @NotNull KlarnaPaymentsSDKError klarnaPaymentsSDKError) {
        Log.e("TAG", klarnaPaymentsSDKError.toString());
    }

    private PaymentViewWrapper wrapperForPaymentView(KlarnaPaymentView paymentView) {
        for (WeakReference<PaymentViewWrapper> reference : viewToDispatcher.keySet()) {
            PaymentViewWrapper wrapper = reference.get();

            if (wrapper != null && wrapper.paymentView == paymentView) {
                return wrapper;
            }
        }
        return null;
    }

}