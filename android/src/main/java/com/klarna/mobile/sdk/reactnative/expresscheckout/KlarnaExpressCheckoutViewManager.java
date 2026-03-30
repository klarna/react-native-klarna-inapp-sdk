package com.klarna.mobile.sdk.reactnative.expresscheckout;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.view.Choreographer;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.facebook.react.bridge.ReactContext;
import com.facebook.react.uimanager.ThemedReactContext;
import com.facebook.react.uimanager.UIManagerHelper;
import com.facebook.react.uimanager.annotations.ReactProp;
import com.facebook.react.uimanager.events.EventDispatcher;
import com.klarna.mobile.sdk.api.KlarnaEnvironment;
import com.klarna.mobile.sdk.api.KlarnaRegion;
import com.klarna.mobile.sdk.api.button.KlarnaButtonShape;
import com.klarna.mobile.sdk.api.button.KlarnaButtonStyle;
import com.klarna.mobile.sdk.api.button.KlarnaButtonTheme;
import com.klarna.mobile.sdk.api.expresscheckout.KlarnaExpressCheckoutButton;
import com.klarna.mobile.sdk.api.expresscheckout.KlarnaExpressCheckoutButtonOptions;
import com.klarna.mobile.sdk.api.expresscheckout.KlarnaExpressCheckoutButtonStyleConfiguration;
import com.klarna.mobile.sdk.api.expresscheckout.KlarnaExpressCheckoutSessionOptions;
import com.klarna.mobile.sdk.reactnative.common.ui.YogaLayoutHelper;
import com.klarna.mobile.sdk.reactnative.spec.RNKlarnaExpressCheckoutViewSpec;

import java.util.Map;
import java.util.WeakHashMap;

public class KlarnaExpressCheckoutViewManager extends RNKlarnaExpressCheckoutViewSpec<FrameLayout> {

    public static final String REACT_CLASS = "RNKlarnaExpressCheckoutView";

    private final Map<FrameLayout, EventDispatcher> viewToDispatcher;
    private final KlarnaExpressCheckoutViewEventSender eventSender;

    // Stored props per view.
    private final Map<FrameLayout, ExpressCheckoutProps> viewToProps;

    // Tracks whether button creation has been scheduled/completed for this view instance.
    private final Map<FrameLayout, Boolean> pendingCreate;
    private final Map<FrameLayout, Boolean> isButtonCreated;

    // Monotonic generation per view used to invalidate stale async callbacks.
    private final Map<FrameLayout, Integer> viewToGeneration;

    // Stores the layout change listener per view so it can be removed on teardown.
    private final Map<FrameLayout, View.OnLayoutChangeListener> viewToLayoutListener;

    // Strong reference to the callback — the SDK stores it via WeakReference internally,
    // so without this the callback would be garbage collected.
    private final Map<FrameLayout, KlarnaExpressCheckoutViewCallback> viewToCallback;

    public KlarnaExpressCheckoutViewManager() {
        super();
        viewToDispatcher = new WeakHashMap<>();
        viewToProps = new WeakHashMap<>();
        pendingCreate = new WeakHashMap<>();
        isButtonCreated = new WeakHashMap<>();
        viewToGeneration = new WeakHashMap<>();
        viewToLayoutListener = new WeakHashMap<>();
        viewToCallback = new WeakHashMap<>();
        eventSender = new KlarnaExpressCheckoutViewEventSender(viewToDispatcher);
    }

    @NonNull
    @Override
    public String getName() {
        return REACT_CLASS;
    }

    @NonNull
    @Override
    protected FrameLayout createViewInstance(@NonNull ThemedReactContext themedReactContext) {
        FrameLayout view = new FrameLayout(themedReactContext);
        view.setBackgroundColor(Color.TRANSPARENT);

        EventDispatcher eventDispatcher = UIManagerHelper.getEventDispatcherForReactTag(
                (ReactContext) view.getContext(), view.getId());
        viewToDispatcher.put(view, eventDispatcher);
        viewToProps.put(view, new ExpressCheckoutProps());
        viewToGeneration.put(view, 0);

        view.addOnAttachStateChangeListener(new View.OnAttachStateChangeListener() {
            @Override
            public void onViewAttachedToWindow(@NonNull View v) {
            }

            @Override
            public void onViewDetachedFromWindow(@NonNull View v) {
                tearDownButton(view);
            }
        });

        return view;
    }

    @Nullable
    @Override
    public Map<String, Object> getExportedCustomDirectEventTypeConstants() {
        return eventSender.getExportedCustomDirectEventTypeConstants();
    }

    // region ReactProp setters
    // Each setter stores the value in ExpressCheckoutProps and schedules (re)creation.
    // The creation is deferred to the next frame so React can finish batching prop updates.

    @ReactProp(name = "sessionType")
    @Override
    public void setSessionType(FrameLayout view, @Nullable String value) {
        ExpressCheckoutProps props = viewToProps.get(view);
        if (props != null) {
            props.sessionType = value;
            scheduleButtonCreation(view);
        }
    }

    @ReactProp(name = "clientId")
    @Override
    public void setClientId(FrameLayout view, @Nullable String value) {
        ExpressCheckoutProps props = viewToProps.get(view);
        if (props != null) {
            props.clientId = value;
            scheduleButtonCreation(view);
        }
    }

    @ReactProp(name = "clientToken")
    @Override
    public void setClientToken(FrameLayout view, @Nullable String value) {
        ExpressCheckoutProps props = viewToProps.get(view);
        if (props != null) {
            props.clientToken = value;
            scheduleButtonCreation(view);
        }
    }

    @ReactProp(name = "locale")
    @Override
    public void setLocale(FrameLayout view, @Nullable String value) {
        ExpressCheckoutProps props = viewToProps.get(view);
        if (props != null) {
            props.locale = value;
            scheduleButtonCreation(view);
        }
    }

    @ReactProp(name = "environment")
    @Override
    public void setEnvironment(FrameLayout view, @Nullable String value) {
        ExpressCheckoutProps props = viewToProps.get(view);
        if (props != null) {
            props.environment = value;
            scheduleButtonCreation(view);
        }
    }

    @ReactProp(name = "region")
    @Override
    public void setRegion(FrameLayout view, @Nullable String value) {
        ExpressCheckoutProps props = viewToProps.get(view);
        if (props != null) {
            props.region = value;
            scheduleButtonCreation(view);
        }
    }

    @ReactProp(name = "returnUrl")
    @Override
    public void setReturnUrl(FrameLayout view, @Nullable String value) {
        ExpressCheckoutProps props = viewToProps.get(view);
        if (props != null) {
            props.returnUrl = value;
            scheduleButtonCreation(view);
        }
    }

    @ReactProp(name = "theme")
    @Override
    public void setTheme(FrameLayout view, @Nullable String value) {
        ExpressCheckoutProps props = viewToProps.get(view);
        if (props != null) {
            props.theme = value;
            scheduleButtonCreation(view);
        }
    }

    @ReactProp(name = "shape")
    @Override
    public void setShape(FrameLayout view, @Nullable String value) {
        ExpressCheckoutProps props = viewToProps.get(view);
        if (props != null) {
            props.shape = value;
            scheduleButtonCreation(view);
        }
    }

    @ReactProp(name = "buttonStyle")
    @Override
    public void setButtonStyle(FrameLayout view, @Nullable String value) {
        ExpressCheckoutProps props = viewToProps.get(view);
        if (props != null) {
            props.buttonStyle = value;
            scheduleButtonCreation(view);
        }
    }

    @ReactProp(name = "autoFinalize")
    @Override
    public void setAutoFinalize(FrameLayout view, boolean value) {
        ExpressCheckoutProps props = viewToProps.get(view);
        if (props != null) {
            props.autoFinalize = value;
            scheduleButtonCreation(view);
        }
    }

    @ReactProp(name = "collectShippingAddress")
    @Override
    public void setCollectShippingAddress(FrameLayout view, boolean value) {
        ExpressCheckoutProps props = viewToProps.get(view);
        if (props != null) {
            props.collectShippingAddress = value;
            scheduleButtonCreation(view);
        }
    }

    @ReactProp(name = "sessionData")
    @Override
    public void setSessionData(FrameLayout view, @Nullable String value) {
        ExpressCheckoutProps props = viewToProps.get(view);
        if (props != null) {
            props.sessionData = value;
            scheduleButtonCreation(view);
        }
    }

    // endregion

    @Override
    public void onDropViewInstance(@NonNull FrameLayout view) {
        viewToDispatcher.remove(view);
        viewToProps.remove(view);
        pendingCreate.remove(view);
        isButtonCreated.remove(view);
        viewToGeneration.remove(view);
        View.OnLayoutChangeListener listener = viewToLayoutListener.remove(view);
        if (listener != null) {
            view.removeOnLayoutChangeListener(listener);
        }
        viewToCallback.remove(view);
        super.onDropViewInstance(view);
    }

    // region Button creation

    /**
     * Schedules button creation for the next frame so React prop batches can settle first.
     * Subsequent prop changes within the same frame coalesce into a single recreate pass.
     *
     * <p><b>Note:</b> The KlarnaExpressCheckoutButton SDK provides no setters for individual
     * properties — all configuration is passed through the constructor. Therefore every prop
     * change requires a full tear-down and recreation of the button. Merchants should avoid
     * changing props (e.g. theme, sessionData) once the user has tapped the button and an
     * authorization flow may be in progress, as the button will be removed from the hierarchy.
     * The SDK authorization UI runs in its own Activity/BottomSheet, so in practice the
     * underlying React component rarely re-renders during that flow.
     */
    private void scheduleButtonCreation(@NonNull FrameLayout view) {
        if (Boolean.TRUE.equals(pendingCreate.get(view))) {
            return; // already scheduled
        }
        pendingCreate.put(view, Boolean.TRUE);
        Choreographer.getInstance().postFrameCallback(frameTimeNanos -> {
            pendingCreate.remove(view);
            tearDownButton(view);
            createButton(view);
        });
    }

    private void tearDownButton(@NonNull FrameLayout view) {
        // Remove the previous layout change listener to avoid stale references.
        View.OnLayoutChangeListener oldListener = viewToLayoutListener.remove(view);
        if (oldListener != null) {
            view.removeOnLayoutChangeListener(oldListener);
        }
        view.removeAllViews();
        isButtonCreated.remove(view);
    }

    private void createButton(@NonNull FrameLayout view) {
        ExpressCheckoutProps props = viewToProps.get(view);
        if (props == null) {
            return;
        }

        Context context = view.getContext();
        Activity activity = null;
        if (context instanceof ThemedReactContext) {
            activity = ((ThemedReactContext) context).getCurrentActivity();
        }
        Context buttonContext = activity != null ? activity : context;
        String sessionData = props.sessionData != null ? props.sessionData : "";

        int generation = nextGeneration(view);

        try {
            // Build session options based on the explicit sessionType.
            KlarnaExpressCheckoutSessionOptions sessionOptions;
            if ("clientToken".equals(props.sessionType)) {
                sessionOptions = new KlarnaExpressCheckoutSessionOptions.ServerSideSession(
                        props.clientToken != null ? props.clientToken : "",
                        sessionData,
                        props.autoFinalize,
                        props.collectShippingAddress
                );
            } else {
                sessionOptions = new KlarnaExpressCheckoutSessionOptions.ClientSideSession(
                        props.clientId != null ? props.clientId : "",
                        sessionData,
                        props.autoFinalize,
                        props.collectShippingAddress
                );
            }

            // Build style configuration
            KlarnaExpressCheckoutButtonStyleConfiguration styleConfig =
                    new KlarnaExpressCheckoutButtonStyleConfiguration(
                            parseTheme(props.theme),
                            parseShape(props.shape),
                            parseButtonStyle(props.buttonStyle)
                    );

            // Build callback — store a strong reference so the SDK's WeakReference doesn't lose it.
            KlarnaExpressCheckoutViewCallback callback =
                    new KlarnaExpressCheckoutViewCallback(view, eventSender);
            viewToCallback.put(view, callback);

            // Build options
            KlarnaExpressCheckoutButtonOptions options = new KlarnaExpressCheckoutButtonOptions(
                    sessionOptions,
                    callback,
                    props.locale,
                    styleConfig,
                    parseEnvironment(props.environment),
                    parseRegion(props.region),
                    null, // KlarnaTheme
                    null, // resourceEndpoint
                    null  // loggingLevel
            );

            KlarnaExpressCheckoutButton expressButton = new KlarnaExpressCheckoutButton(buttonContext, options);

            view.addView(expressButton, new ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT
            ));

            isButtonCreated.put(view, Boolean.TRUE);

            // React Native's Yoga layout engine suppresses requestLayout() from child views,
            // so the SDK button won't measure/layout correctly without a manual layout pass.
            setupManualLayout(view, expressButton, generation);

            // Send initial resize event after button renders.
            // getHeight() returns pixels; React Native expects density-independent pixels (dp).
            expressButton.post(() -> {
                if (isCurrentGeneration(view, generation)) {
                    maybeSendResizeEvent(view, expressButton);
                }
            });
        } catch (Exception e) {
            Log.e("KlarnaExpressCheckout", "Failed to create express checkout button", e);
            eventSender.sendKlarnaExpressCheckoutError(
                    view,
                    "ButtonCreationError",
                    e.getMessage() != null ? e.getMessage() : "Unknown error during button creation",
                    true
            );
        }
    }

    /**
     * React Native's Yoga layout engine suppresses requestLayout() from native child views.
     * Schedule a manual measure+layout pass every frame until the button is laid out,
     * and re-layout whenever the wrapper's bounds change.
     */
    private void setupManualLayout(
            @NonNull FrameLayout wrapper,
            @NonNull KlarnaExpressCheckoutButton button,
            int generation
    ) {
        final int[] lastHeightPx = new int[]{-1};
        final int[] stableFrameCount = new int[]{0};
        Choreographer.FrameCallback frameCallback = new Choreographer.FrameCallback() {
            @Override
            public void doFrame(long frameTimeNanos) {
                if (!isCurrentGeneration(wrapper, generation) || !button.isAttachedToWindow()) {
                    return;
                }
                YogaLayoutHelper.measureAndLayout(wrapper, button);
                maybeSendResizeEvent(wrapper, button);

                int currentHeightPx = button.getHeight();
                if (currentHeightPx <= 0) {
                    stableFrameCount[0] = 0;
                } else if (currentHeightPx == lastHeightPx[0]) {
                    stableFrameCount[0] = stableFrameCount[0] + 1;
                } else {
                    stableFrameCount[0] = 0;
                }
                lastHeightPx[0] = currentHeightPx;

                // Keep posting while content is still unresolved or changing across frames.
                if (currentHeightPx <= 0 || stableFrameCount[0] < 3) {
                    Choreographer.getInstance().postFrameCallback(this);
                }
            }
        };
        Choreographer.getInstance().postFrameCallback(frameCallback);

        View.OnLayoutChangeListener layoutListener = (v, left, top, right, bottom, oldLeft, oldTop, oldRight, oldBottom) -> {
            if (!isCurrentGeneration(wrapper, generation) || !button.isAttachedToWindow()) {
                return;
            }
            YogaLayoutHelper.measureAndLayout(wrapper, button);
            maybeSendResizeEvent(wrapper, button);
        };
        viewToLayoutListener.put(wrapper, layoutListener);
        wrapper.addOnLayoutChangeListener(layoutListener);
    }

    private int nextGeneration(@NonNull FrameLayout view) {
        int next = (viewToGeneration.get(view) == null ? 0 : viewToGeneration.get(view)) + 1;
        viewToGeneration.put(view, next);
        return next;
    }

    private boolean isCurrentGeneration(@NonNull FrameLayout view, int generation) {
        Integer current = viewToGeneration.get(view);
        return current != null && current == generation;
    }

    private void maybeSendResizeEvent(
            @NonNull FrameLayout wrapper,
            @NonNull View button
    ) {
        if (button.getHeight() <= 0) {
            return;
        }
        float density = wrapper.getResources().getDisplayMetrics().density;
        int heightInDp = Math.round(button.getHeight() / density);
        ExpressCheckoutProps props = viewToProps.get(wrapper);
        if (props == null || props.lastSentHeightDp == heightInDp) {
            return;
        }
        props.lastSentHeightDp = heightInDp;
        eventSender.sendOnResizedEvent(wrapper, heightInDp);
    }

    // endregion

    // region Prop parsing helpers

    @Nullable
    static KlarnaButtonTheme parseTheme(@Nullable String value) {
        if (value == null) return null;
        switch (value) {
            case "light": return KlarnaButtonTheme.LIGHT;
            case "dark": return KlarnaButtonTheme.DARK;
            case "auto": return KlarnaButtonTheme.AUTO;
            default: return null;
        }
    }

    @Nullable
    static KlarnaButtonShape parseShape(@Nullable String value) {
        if (value == null) return null;
        switch (value) {
            case "roundedRect": return KlarnaButtonShape.ROUNDED_RECT;
            case "pill": return KlarnaButtonShape.PILL;
            case "rectangle": return KlarnaButtonShape.RECTANGLE;
            default: return null;
        }
    }

    @Nullable
    static KlarnaButtonStyle parseButtonStyle(@Nullable String value) {
        if (value == null) return null;
        switch (value) {
            case "filled": return KlarnaButtonStyle.FILLED;
            case "outlined": return KlarnaButtonStyle.OUTLINED;
            default: return null;
        }
    }

    @Nullable
    static KlarnaEnvironment parseEnvironment(@Nullable String value) {
        if (value == null) return null;
        switch (value) {
            case "playground": return KlarnaEnvironment.PLAYGROUND;
            case "production": return KlarnaEnvironment.PRODUCTION;
            default: return null;
        }
    }

    @Nullable
    static KlarnaRegion parseRegion(@Nullable String value) {
        if (value == null) return null;
        switch (value) {
            case "eu": return KlarnaRegion.EU;
            case "na": return KlarnaRegion.NA;
            case "oc": return KlarnaRegion.OC;
            default: return null;
        }
    }

    // endregion

    /**
     * Holds the current prop values for a single view instance.
     */
    static class ExpressCheckoutProps {
        String sessionType;
        String clientId;
        String clientToken;
        String locale;
        String environment;
        String region;
        String returnUrl;
        String theme;
        String shape;
        String buttonStyle;
        boolean autoFinalize = true;
        boolean collectShippingAddress = false;
        String sessionData;
        int lastSentHeightDp = -1;
    }
}
