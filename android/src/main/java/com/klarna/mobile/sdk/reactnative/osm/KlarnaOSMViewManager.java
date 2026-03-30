package com.klarna.mobile.sdk.reactnative.osm;

import android.app.Activity;
import android.view.View;
import android.view.ViewTreeObserver;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.facebook.react.bridge.ReactContext;
import com.facebook.react.bridge.ReadableArray;
import com.facebook.react.uimanager.ThemedReactContext;
import com.facebook.react.uimanager.UIManagerHelper;
import com.facebook.react.uimanager.annotations.ReactProp;
import com.facebook.react.uimanager.events.EventDispatcher;
import com.klarna.mobile.sdk.api.osm.KlarnaOSMView;
import com.klarna.mobile.sdk.KlarnaMobileSDKError;
import com.klarna.mobile.sdk.reactnative.common.ui.WrapperView;
import com.klarna.mobile.sdk.reactnative.common.ui.YogaLayoutHelper;
import com.klarna.mobile.sdk.reactnative.spec.RNKlarnaOSMViewSpec;
import com.klarna.mobile.sdk.api.KlarnaRegion;
import com.klarna.mobile.sdk.api.KlarnaEnvironment;
import com.klarna.mobile.sdk.api.KlarnaTextStyleConfiguration;
import com.klarna.mobile.sdk.api.KlarnaTheme;
import com.klarna.mobile.sdk.api.osm.KlarnaOSMStyleConfiguration;

import com.facebook.react.uimanager.PixelUtil;

import java.util.Map;
import java.util.WeakHashMap;

public class KlarnaOSMViewManager extends RNKlarnaOSMViewSpec<WrapperView<KlarnaOSMView>> {

    public static final String KLARNA_OSM_VIEW_REACT_CLASS = "RNKlarnaOSMView";
    public static final String COMMAND_RENDER_OSM = "render";

    private final Map<WrapperView<KlarnaOSMView>, EventDispatcher> viewToDispatcher;
    private final KlarnaOSMViewEventSender eventSender;
    private final KlarnaOSMViewEventHandler eventHandler;
    private final Map<KlarnaOSMView, ViewTreeObserver.OnGlobalLayoutListener> layoutListeners = new WeakHashMap<>();
    private final Map<KlarnaOSMView, String> pendingBackgroundColor = new WeakHashMap<>();
    private final Map<KlarnaOSMView, String> pendingTextColor = new WeakHashMap<>();

    public KlarnaOSMViewManager() {
        super();
        viewToDispatcher = new WeakHashMap<>();
        eventSender = new KlarnaOSMViewEventSender(viewToDispatcher);
        eventHandler = new KlarnaOSMViewEventHandler(eventSender);
    }

    @NonNull
    @Override
    public String getName() {
        return KLARNA_OSM_VIEW_REACT_CLASS;
    }

    @NonNull
    @Override
    protected WrapperView<KlarnaOSMView> createViewInstance(@NonNull ThemedReactContext themedReactContext) {
        Activity activity = themedReactContext.getCurrentActivity();
        KlarnaOSMView osmView = new KlarnaOSMView(activity != null ? activity : themedReactContext);
        osmView.setEventHandler(eventHandler);
        if (activity != null) {
            osmView.setHostActivity(activity);
        }
        WrapperView<KlarnaOSMView> view = new WrapperView<>(themedReactContext, null, osmView);

        EventDispatcher eventDispatcher = UIManagerHelper.getEventDispatcherForReactTag((ReactContext) view.getContext(), view.getId());
        viewToDispatcher.put(view, eventDispatcher);

        view.addOnAttachStateChangeListener(new View.OnAttachStateChangeListener() {
            @Override
            public void onViewAttachedToWindow(@NonNull View v) {
                eventSender.sendOnOSMViewReadyEvent(osmView);
                v.removeOnAttachStateChangeListener(this);
            }

            @Override
            public void onViewDetachedFromWindow(@NonNull View v) {
                removeLayoutListener(osmView);
            }
        });

        return view;
    }

    @Override
    public void onDropViewInstance(@NonNull WrapperView<KlarnaOSMView> view) {
        removeLayoutListener(view.getView());
        viewToDispatcher.remove(view);
        super.onDropViewInstance(view);
    }

    private void removeLayoutListener(@Nullable KlarnaOSMView osmView) {
        if (osmView == null) {
            return;
        }
        ViewTreeObserver.OnGlobalLayoutListener listener = layoutListeners.remove(osmView);
        if (listener != null) {
            osmView.getViewTreeObserver().removeOnGlobalLayoutListener(listener);
        }
    }

    @Nullable
    @Override
    public Map<String, Object> getExportedCustomDirectEventTypeConstants() {
        return eventSender.getExportedCustomDirectEventTypeConstants();
    }

    @Override
    public void receiveCommand(@NonNull WrapperView<KlarnaOSMView> root, String commandId, @Nullable ReadableArray args) {
        if (COMMAND_RENDER_OSM.equals(commandId)) {
            render(root);
        }
    }

    @ReactProp(name = "clientId")
    @Override
    public void setClientId(WrapperView<KlarnaOSMView> view, @Nullable String value) {
        KlarnaOSMView osmView = view.getView();
        if (osmView != null && value != null && !value.isEmpty()) {
            osmView.setClientId(value);
        }
    }

    @ReactProp(name = "placementKey")
    @Override
    public void setPlacementKey(WrapperView<KlarnaOSMView> view, @Nullable String value) {
        KlarnaOSMView osmView = view.getView();
        if (osmView != null && value != null && !value.isEmpty()) {
            osmView.setPlacementKey(value);
        }
    }

    @ReactProp(name = "locale")
    @Override
    public void setLocale(WrapperView<KlarnaOSMView> view, @Nullable String value) {
        KlarnaOSMView osmView = view.getView();
        if (osmView != null && value != null && !value.isEmpty()) {
            osmView.setLocale(value);
        }
    }

    @ReactProp(name = "purchaseAmount")
    @Override
    public void setPurchaseAmount(WrapperView<KlarnaOSMView> view, @Nullable String value) {
        KlarnaOSMView osmView = view.getView();
        if (osmView != null && value != null && !value.isEmpty()) {
            try {
                long amount = Long.parseLong(value);
                osmView.setPurchaseAmount(amount);
            } catch (NumberFormatException e) {
                eventSender.sendError(osmView, "InvalidPurchaseAmount",
                        "Invalid purchaseAmount: '" + value + "'. Expected a numeric string.", false);
            }
        }
    }

    @ReactProp(name = "environment")
    @Override
    public void setEnvironment(WrapperView<KlarnaOSMView> view, @Nullable String value) {
        KlarnaOSMView osmView = view.getView();
        if (osmView != null && value != null && !value.isEmpty()) {
            switch (value) {
                case "playground":
                    osmView.setEnvironment(KlarnaEnvironment.PLAYGROUND);
                    break;
                case "production":
                    osmView.setEnvironment(KlarnaEnvironment.PRODUCTION);
                    break;
                case "staging":
                    osmView.setEnvironment(KlarnaEnvironment.STAGING);
                    break;
                default:
                    break;
            }
        }
    }

    @ReactProp(name = "region")
    @Override
    public void setRegion(WrapperView<KlarnaOSMView> view, @Nullable String value) {
        KlarnaOSMView osmView = view.getView();
        if (osmView != null && value != null && !value.isEmpty()) {
            switch (value) {
                case "eu":
                    osmView.setRegion(KlarnaRegion.EU);
                    break;
                case "na":
                    osmView.setRegion(KlarnaRegion.NA);
                    break;
                case "oc":
                    osmView.setRegion(KlarnaRegion.OC);
                    break;
                default:
                    break;
            }
        }
    }

    @ReactProp(name = "theme")
    @Override
    public void setTheme(WrapperView<KlarnaOSMView> view, @Nullable String value) {
        KlarnaOSMView osmView = view.getView();
        if (osmView != null && value != null && !value.isEmpty()) {
            switch (value) {
                case "light":
                    osmView.setTheme(KlarnaTheme.LIGHT);
                    break;
                case "dark":
                    osmView.setTheme(KlarnaTheme.DARK);
                    break;
                case "automatic":
                    osmView.setTheme(KlarnaTheme.AUTOMATIC);
                    break;
                default:
                    break;
            }
        }
    }

    @ReactProp(name = "backgroundColor")
    @Override
    public void setBackgroundColor(WrapperView<KlarnaOSMView> view, @Nullable String value) {
        KlarnaOSMView osmView = view.getView();
        if (osmView != null) {
            pendingBackgroundColor.put(osmView, value);
            updateStyleConfiguration(osmView);
        }
    }

    @ReactProp(name = "textColor")
    @Override
    public void setTextColor(WrapperView<KlarnaOSMView> view, @Nullable String value) {
        KlarnaOSMView osmView = view.getView();
        if (osmView != null) {
            pendingTextColor.put(osmView, value);
            updateStyleConfiguration(osmView);
        }
    }

    @ReactProp(name = "returnUrl")
    @Override
    public void setReturnUrl(WrapperView<KlarnaOSMView> view, @Nullable String value) {
        // No need to do anything here because KlarnaOSMView ignores the return URL set by merchant and uses an internally created return URL.
    }

    @Override
    public void render(WrapperView<KlarnaOSMView> view) {
        KlarnaOSMView osmView = view.getView();
        if (osmView != null) {
            ReactContext reactContext = (ReactContext) view.getContext();
            Activity activity = reactContext.getCurrentActivity();
            if (activity != null) {
                osmView.setHostActivity(activity);
            }
            osmView.render(error -> {
                if (error != null) {
                    eventSender.sendKlarnaMobileSDKError(osmView, error);
                } else {
                    removeLayoutListener(osmView);
                    addLayoutListenerForMeasure(osmView, view);
                }
            });
        }
    }

    private boolean tryMeasureContent(KlarnaOSMView osmView, WrapperView<KlarnaOSMView> wrapperView) {
        if (osmView.getChildCount() > 0 && wrapperView.getWidth() > 0) {
            View child = osmView.getChildAt(0);
            YogaLayoutHelper.measureAndLayout(wrapperView, child);
            int contentHeight = child.getMeasuredHeight();
            if (contentHeight > 0) {
                int contentHeightDp = Math.round(PixelUtil.toDIPFromPixel(contentHeight));
                eventSender.sendOnResizedEvent(osmView, contentHeightDp);
                return true;
            }
        }
        return false;
    }

    private void updateStyleConfiguration(KlarnaOSMView osmView) {
        String bgColorStr = pendingBackgroundColor.get(osmView);
        String txtColorStr = pendingTextColor.get(osmView);

        Integer bgColor = parseColor(bgColorStr);
        Integer txtColor = parseColor(txtColorStr);

        if (bgColor != null || txtColor != null) {
            KlarnaOSMStyleConfiguration.Builder builder = new KlarnaOSMStyleConfiguration.Builder();
            if (bgColor != null) {
                builder.setBackgroundColor(bgColor);
            }
            if (txtColor != null) {
                KlarnaTextStyleConfiguration textStyle = new KlarnaTextStyleConfiguration.Builder()
                        .setTextColor(txtColor)
                        .build();
                builder.setTextStyle(textStyle);
            }
            osmView.setStyleConfiguration(builder.build());
        } else {
            osmView.setStyleConfiguration(null);
        }
    }

    @Nullable
    private Integer parseColor(@Nullable String colorString) {
        if (colorString == null || colorString.isEmpty()) {
            return null;
        }
        try {
            return android.graphics.Color.parseColor(colorString.startsWith("#") ? colorString : "#" + colorString);
        } catch (IllegalArgumentException e) {
            return null;
        }
    }

    /**
     * Adds a ViewTreeObserver listener to measure content when layout is ready.
     * The listener automatically removes itself after successful measurement.
     */
    private void addLayoutListenerForMeasure(KlarnaOSMView osmView, WrapperView<KlarnaOSMView> wrapperView) {
        // Try measuring on the next frame — content may already be ready and no further
        // layout event would fire, so the ViewTreeObserver alone is not sufficient.
        osmView.post(() -> {
            if (!osmView.isAttachedToWindow()) {
                return;
            }
            if (tryMeasureContent(osmView, wrapperView)) {
                removeLayoutListener(osmView);
            }
        });

        ViewTreeObserver.OnGlobalLayoutListener listener = new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                if (!osmView.isAttachedToWindow()) {
                    removeThisListener();
                    return;
                }

                if (tryMeasureContent(osmView, wrapperView)) {
                    removeThisListener();
                }
            }

            private void removeThisListener() {
                layoutListeners.remove(osmView);
                osmView.getViewTreeObserver().removeOnGlobalLayoutListener(this);
            }
        };

        layoutListeners.put(osmView, listener);
        osmView.getViewTreeObserver().addOnGlobalLayoutListener(listener);
    }
}
