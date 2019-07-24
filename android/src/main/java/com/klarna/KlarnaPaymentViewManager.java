package com.klarna;

import android.app.Application;

import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReadableArray;
import com.facebook.react.common.MapBuilder;
import com.facebook.react.uimanager.SimpleViewManager;
import com.facebook.react.uimanager.ThemedReactContext;
import com.facebook.react.uimanager.UIManagerModule;
import com.facebook.react.uimanager.annotations.ReactProp;
import com.facebook.react.uimanager.events.EventDispatcher;
import com.klarna.mobile.sdk.payments.KlarnaPaymentError;
import com.klarna.mobile.sdk.payments.KlarnaPaymentViewCallBack;
import com.klarna.mobile.sdk.payments.KlarnaPaymentsSDK;
import com.klarna.mobile.sdk.payments.PaymentView;

import org.jetbrains.annotations.NotNull;

import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * Wraps the payment views being exposed to JS.
 */
public class KlarnaPaymentViewManager extends SimpleViewManager<PaymentView> {

    public static final int COMMAND_INITIALIZE = 1;
    public static final int COMMAND_LOAD = 2;
    public static final int COMMAND_LOAD_PAYMENT_REVIEW = 3;
    public static final int COMMAND_AUTHORIZE = 4;
    public static final int COMMAND_REAUTHORIZE = 5;
    public static final int COMMAND_FINALIZE = 6;

    public static final String REACT_CLASS = "KlarnaPaymentView";

    private final Application app;

    // Store a list of views to event dispatchers so we send up events via the right views.
    private Map<WeakReference<PaymentView>, EventDispatcher> viewToDispatcher;

    KlarnaPaymentViewManager(ReactApplicationContext reactApplicationContext, Application app) {
        super();
        this.viewToDispatcher = new HashMap<>();
        this.app = app;
    }

    @Override
    public String getName() {
        return REACT_CLASS;
    }

    @Override
    public PaymentView createViewInstance(ThemedReactContext context) {
        KlarnaPaymentsSDK.initialize(app);
        PaymentView view = new PaymentView(context, null);

        EventDispatcher dispatcher = context.getNativeModule(UIManagerModule.class).getEventDispatcher();
        viewToDispatcher.put(new WeakReference<PaymentView>(view), dispatcher);

        return view;
    }

    /**
     * Commads are methods that RN will expose on the JS side for a view. They can be called via
     * `UIManager.dispatchViewManagerCommand` from react.
     * @return
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
    public void receiveCommand(@Nonnull PaymentView root, int commandId, @Nullable ReadableArray args) {
        switch (commandId) {
            case COMMAND_INITIALIZE:
                if (args != null) {
                    final String token = args.getString(0);
                    final String returnUrl = args.getString(1);

                    if (token != null && returnUrl != null) {
                        root.initialize(token, returnUrl);
                    }
                }
                break;

            case COMMAND_LOAD:
                root.load(null);
                break;

            case COMMAND_LOAD_PAYMENT_REVIEW:
                root.loadPaymentReview();
                break;

            case COMMAND_AUTHORIZE:
                if (args != null) {
                    final boolean autoFinalize = args.getBoolean(0);
                    root.authorize(autoFinalize, null);
                }
                break;

            case COMMAND_REAUTHORIZE:
                root.reauthorize(null);
                break;

            case COMMAND_FINALIZE:
                root.finalize(null);
                break;

        }

    }

    /**
     * Exposes direct event types that will be accessible as prop "callbacks" on react native.
     *
     * Structure must follow:
     *  {
     *      "<eventName>": {"registrationName": "<eventName>"}
     *  }
     */
    @Nullable
    @Override
    public Map getExportedCustomDirectEventTypeConstants() {
        return MapBuilder.of(
                KlarnaPaymentEvent.EVENT_NAME_ON_CHANGE, MapBuilder.of("registrationName", KlarnaPaymentEvent.EVENT_NAME_ON_CHANGE)
        );
    }

    /**
     * Payment View's payment method category (e.g: "pay_later", "pay_over_time").
     *
     * @param view
     * @param category
     */
    @ReactProp(name = "category")
    public void setCategory(PaymentView view, String category) {
        view.registerPaymentViewCallback(callback);
        view.setCategory(category);
    }

    private KlarnaPaymentViewCallBack callback = new KlarnaPaymentViewCallBack() {

        @Override
        public void onInitialized(@NotNull PaymentView paymentView) {
            postEvent(new KlarnaPaymentEvent(paymentView.getId(), "initialized", null), paymentView);
        }

        @Override
        public void onLoaded(@NotNull PaymentView paymentView) {
            postEvent(new KlarnaPaymentEvent(paymentView.getId(), "loaded", null), paymentView);

        }

        @Override
        public void onLoadPaymentReview(@NotNull PaymentView paymentView, boolean b) {

        }

        @Override
        public void onAuthorized(@NotNull PaymentView paymentView, boolean approved, @org.jetbrains.annotations.Nullable String authToken, @org.jetbrains.annotations.Nullable Boolean finalizeRequired) {
            postEvent(new KlarnaPaymentEvent(paymentView.getId(), "authorized", MapBuilder.<String, Object>of("approved", approved, "authToken", authToken, "finalizeRequired", finalizeRequired)), paymentView);
        }

        @Override
        public void onReauthorized(@NotNull PaymentView paymentView, boolean b, @org.jetbrains.annotations.Nullable String s) {

        }

        @Override
        public void onFinalized(@NotNull PaymentView paymentView, boolean b, @org.jetbrains.annotations.Nullable String s) {

        }

        @Override
        public void onErrorOccurred(@NotNull PaymentView paymentView, @NotNull KlarnaPaymentError klarnaPaymentError) {

        }
    };

    /**
     * Sends an event via a specific view's dispatcher (that we stored above).
     *
     * @param event
     * @param view
     */
    private void postEvent(KlarnaPaymentEvent event, PaymentView view) {
        EventDispatcher foundDispatcher = null;
        for (WeakReference<PaymentView> reference: viewToDispatcher.keySet()) {
            if (reference.get() == view) {
                foundDispatcher = viewToDispatcher.get(reference);
                break;
            }
        }

        if (foundDispatcher != null) {
            foundDispatcher.dispatchEvent(event);
        }
    }
}