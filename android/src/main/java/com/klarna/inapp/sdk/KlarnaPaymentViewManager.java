package com.klarna.inapp.sdk;

import android.app.Application;

import androidx.annotation.NonNull;

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
import com.klarna.mobile.sdk.api.payments.KlarnaPaymentView;
import com.klarna.mobile.sdk.api.payments.KlarnaPaymentViewCallback;
import com.klarna.mobile.sdk.api.payments.KlarnaPaymentsSDKError;

import org.jetbrains.annotations.NotNull;

import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.Nullable;

/**
 * Wraps the Payment Views being exposed to JS.
 */
public class KlarnaPaymentViewManager extends RNKlarnaPaymentViewSpec<PaymentViewWrapper> implements KlarnaPaymentViewCallback {


    // Commands that can be triggered from RN
    public static final String COMMAND_INITIALIZE = "initialize";
    public static final String COMMAND_LOAD = "load";
    public static final String COMMAND_LOAD_PAYMENT_REVIEW = "loadPaymentReview";
    public static final String COMMAND_AUTHORIZE = "authorize";
    public static final String COMMAND_REAUTHORIZE = "reauthorize";
    public static final String COMMAND_FINALIZE = "finalize";

    public static final String REACT_CLASS = "RNKlarnaPaymentView";

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
        EventDispatcher dispatcher = UIManagerHelper.getEventDispatcherForReactTag((ReactContext) view.getContext(), view.getId());
        viewToDispatcher.put(new WeakReference<>(view), dispatcher);

        return view;
    }


    /**
     * Handles commands received from RN to a specific view.
     *
     * @param root
     * @param commandId
     * @param args
     */
    @Override
    public void receiveCommand(@NonNull PaymentViewWrapper root, String commandId, @androidx.annotation.Nullable ReadableArray args) {
        String sessionData = null;

        switch (commandId) {
            case COMMAND_INITIALIZE:
                if (args != null) {
                    final String token = args.getString(0);
                    final String returnUrl = args.getString(1);
                    initialize(root, token, returnUrl);
                }
                break;

            case COMMAND_LOAD:
                if (args != null) {
                    sessionData = args.getString(0);
                }
                load(root, sessionData);
                break;

            case COMMAND_LOAD_PAYMENT_REVIEW:
                loadPaymentReview(root);
                break;

            case COMMAND_AUTHORIZE:
                if (args != null) {
                    final boolean autoFinalize = args.getBoolean(0);
                    sessionData = args.getString(1);

                    authorize(root, autoFinalize, sessionData);
                }
                break;

            case COMMAND_REAUTHORIZE:
                if (args != null) {
                    sessionData = args.getString(0);
                }
                reauthorize(root, sessionData);
                break;

            case COMMAND_FINALIZE:
                if (args != null) {
                    sessionData = args.getString(0);
                }
                finalize(root, sessionData);
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
                KlarnaPaymentEvent.EVENT_TYPE_NAME_ON_INITIALIZE, MapBuilder.of("registrationName", KlarnaPaymentEvent.EVENT_NAME_ON_INITIALIZE),
                KlarnaPaymentEvent.EVENT_TYPE_NAME_ON_LOAD, MapBuilder.of("registrationName", KlarnaPaymentEvent.EVENT_NAME_ON_LOAD),
                KlarnaPaymentEvent.EVENT_TYPE_NAME_ON_LOAD_PAYMENT_REVIEW, MapBuilder.of("registrationName", KlarnaPaymentEvent.EVENT_NAME_ON_LOAD_PAYMENT_REVIEW),
                KlarnaPaymentEvent.EVENT_TYPE_NAME_ON_AUTHORIZE, MapBuilder.of("registrationName", KlarnaPaymentEvent.EVENT_NAME_ON_AUTHORIZE),
                KlarnaPaymentEvent.EVENT_TYPE_NAME_ON_REAUTHORIZE, MapBuilder.of("registrationName", KlarnaPaymentEvent.EVENT_NAME_ON_REAUTHORIZE),
                KlarnaPaymentEvent.EVENT_TYPE_NAME_ON_FINALIZE, MapBuilder.of("registrationName", KlarnaPaymentEvent.EVENT_NAME_ON_FINALIZE),
                KlarnaPaymentEvent.EVENT_TYPE_NAME_ON_ERROR, MapBuilder.of("registrationName", KlarnaPaymentEvent.EVENT_NAME_ON_ERROR)
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

    @Override
    public void initialize(PaymentViewWrapper view, String clientToken, String returnUrl) {
        if (clientToken != null && returnUrl != null) {
            view.paymentView.initialize(clientToken, returnUrl);
        }
    }

    @Override
    public void load(PaymentViewWrapper view, String sessionData) {
        view.load(sessionData.length() > 0 ? sessionData : null);
    }

    @Override
    public void loadPaymentReview(PaymentViewWrapper view) {
        view.paymentView.loadPaymentReview();
    }

    @Override
    public void authorize(PaymentViewWrapper view, boolean autoFinalize, String sessionData) {
        view.paymentView.authorize(autoFinalize, sessionData.length() > 0 ? sessionData : null);
    }

    @Override
    public void reauthorize(PaymentViewWrapper view, String sessionData) {
        view.paymentView.reauthorize(sessionData.length() > 0 ? sessionData : null);
    }

    @Override
    public void finalize(PaymentViewWrapper view, String sessionData) {
        view.paymentView.finalize(sessionData.length() > 0 ? sessionData : null);
    }

    /**
     * Creates an event from event name and a map of params. Sends it via the right dispatcher.
     * @param eventName
     * @param additionalParams
     * @param view
     */
    private void postEventForView(String eventName, WritableMap additionalParams, KlarnaPaymentView view) {
        PaymentViewWrapper wrapper = wrapperForPaymentView(view);
        KlarnaPaymentEvent event = new KlarnaPaymentEvent(wrapper.getId(), eventName, additionalParams);
        UIManagerHelper.getEventDispatcherForReactTag((ReactContext) wrapper.getContext(), wrapper.getId()).dispatchEvent(event);
    }

    /**
     * Calls requestLayout on the wrapper to fetch height of the contents.
     * @param view
     */
    private void requestLayout(KlarnaPaymentView view) {
        if (view != null) {
            PaymentViewWrapper wrapper = wrapperForPaymentView(view);
            if (wrapper != null) {
                wrapper.requestLayout();
            }
        }
    }


    /* -- KlarnaPaymentView callback methods -- */

    @Override
    public void onInitialized(@NotNull KlarnaPaymentView paymentView) {
        requestLayout(paymentView);
        postEventForView(KlarnaPaymentEvent.EVENT_TYPE_NAME_ON_INITIALIZE, null, paymentView);
    }

    @Override
    public void onLoaded(@NotNull KlarnaPaymentView paymentView) {
        requestLayout(paymentView);
        postEventForView(KlarnaPaymentEvent.EVENT_TYPE_NAME_ON_LOAD, null, paymentView);
    }

    @Override
    public void onLoadPaymentReview(@NotNull KlarnaPaymentView paymentView, boolean b) {
        requestLayout(paymentView);
        postEventForView(KlarnaPaymentEvent.EVENT_TYPE_NAME_ON_LOAD_PAYMENT_REVIEW, null, paymentView);
    }

    @Override
    public void onAuthorized(@NotNull KlarnaPaymentView paymentView, boolean approved, @org.jetbrains.annotations.Nullable String authToken, @org.jetbrains.annotations.Nullable Boolean finalizeRequired) {
        requestLayout(paymentView);
        WritableMap params = Arguments.createMap();
        params.putBoolean("approved", approved);
        params.putString("authToken", authToken == null ? "" : authToken);
        params.putBoolean("finalizeRequired", finalizeRequired);
        postEventForView(KlarnaPaymentEvent.EVENT_TYPE_NAME_ON_AUTHORIZE,
                params,
                paymentView);
    }

    @Override
    public void onReauthorized(@NotNull KlarnaPaymentView paymentView, boolean approved, @org.jetbrains.annotations.Nullable String authToken) {
        requestLayout(paymentView);
        WritableMap params = Arguments.createMap();
        params.putBoolean("approved", approved);
        params.putString("authToken", authToken == null ? "" : authToken);
        postEventForView(KlarnaPaymentEvent.EVENT_TYPE_NAME_ON_REAUTHORIZE,
                params,
                paymentView);
    }

    @Override
    public void onFinalized(@NotNull KlarnaPaymentView paymentView, boolean approved, @org.jetbrains.annotations.Nullable String authToken) {
        requestLayout(paymentView);
        WritableMap params = Arguments.createMap();
        params.putBoolean("approved", approved);
        params.putString("authToken", authToken == null ? "" : authToken);
        postEventForView(KlarnaPaymentEvent.EVENT_TYPE_NAME_ON_FINALIZE,
                params,
                paymentView);
    }

    @Override
    public void onErrorOccurred(@NotNull KlarnaPaymentView klarnaPaymentView, @NotNull KlarnaPaymentsSDKError klarnaPaymentsSDKError) {
        requestLayout(klarnaPaymentView);
        MappableKlarnaPaymentsSDKError sdkError = new MappableKlarnaPaymentsSDKError(klarnaPaymentsSDKError);
        WritableMap params = Arguments.createMap();
        params.putMap("error", (ReadableMap) sdkError.buildMap());
        postEventForView(KlarnaPaymentEvent.EVENT_TYPE_NAME_ON_ERROR,
                params,
                klarnaPaymentView);
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
