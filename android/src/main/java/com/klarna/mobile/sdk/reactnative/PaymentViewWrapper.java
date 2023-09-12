package com.klarna.mobile.sdk.reactnative;

import android.util.AttributeSet;
import android.view.View;
import android.webkit.WebView;
import android.widget.LinearLayout;

import com.facebook.react.bridge.GuardedRunnable;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.uimanager.UIManagerModule;
import com.klarna.mobile.sdk.api.payments.KlarnaPaymentView;
import com.klarna.mobile.sdk.api.payments.KlarnaPaymentViewCallback;

/***
 * Wraps the KlarnaPaymentView so we can see when a requestLayout() has been triggered.
 */
public class PaymentViewWrapper extends LinearLayout implements HeightListener.HeightListenerCallback {
    private float displayDensity = 1;
    public KlarnaPaymentView paymentView;
    private boolean loadCalled = false;
    private HeightListener heightListener;

    public PaymentViewWrapper(ReactApplicationContext context, AttributeSet attrs, KlarnaPaymentViewCallback paymentViewCallback) {
        super(context, attrs);
        // Get density for resizing.
        displayDensity = context.getResources().getDisplayMetrics().density;
        // Add KlarnaPaymentView
        LinearLayout.LayoutParams webViewParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
        paymentView = new KlarnaPaymentView(getReactAppContext().getCurrentActivity(), attrs); // Insure we use activity and not application context for dialogs.
        paymentView.registerPaymentViewCallback(paymentViewCallback);
        addView(paymentView, webViewParams);
        heightListener = new HeightListener(getPaymentViewWebView(), this);
    }

    public void load(String sessionData) {
        paymentView.load(sessionData);
        loadCalled = true;
        heightListener.injectListener(getPaymentViewWebView());
    }

    @Override
    public void requestLayout() {
        super.requestLayout();
        if (isReady() && loadCalled) {
            heightListener.fetchHeight(getPaymentViewWebView());
        }
    }

    /**
     * The web view inside the KlarnaPaymentView will trigger requestLayout(), but there is no way
     * to get the KlarnaPaymentView's correct height.
     * <p>
     * Attempts include:
     * - Using Android's measure() and co. methods
     * - Getting the WebView's getContentHeight()
     * - Using a variety of layout listeners.
     * <p>
     * So instead we're:
     * 1. Evaluating some JS (yes, making things even more fragile).
     * 2. Triggering a size change via the UIManagerModule. This will effectively apply a fixed size
     * via width and height style attributes on the React side of things.
     * <p>
     * Note: We can apply style by setting props with uimm.updateView() to just set a height (and
     * no width), but it doesn't update immediately or with the right height.
     * <p>
     * The width will be whatever the parent component's width is.
     */
    private void setHeight(String height) {
        if (height == null || height.equals("null") || height.equals("undefined")) {
            return;
        }
        try {
            final int width = getParentViewWidth();
            final float contentHeight = Float.parseFloat(height);
            final float scaledHeight = contentHeight * displayDensity;
            getReactAppContext().runOnNativeModulesQueueThread(new GuardedRunnable(getReactAppContext()) {
                @Override
                public void runGuarded() {
                    UIManagerModule uimm = getReactAppContext().getNativeModule(UIManagerModule.class);
                    uimm.updateNodeSize(getId(), width, (int) scaledHeight);
                }
            });
        } catch (Throwable t) {
        }
    }

    /**
     * Returns true if the payment view is part of the hierarchy (the first requestLayout() is
     * triggered before) and the whole view is part of the RN hierarchy.
     *
     * @return true if ready
     */
    private boolean isReady() {
        return paymentView != null && getId() != -1;
    }

    /**
     * Returns the parent view's width.
     */
    private int getParentViewWidth() {
        View parentReactView = (View) getParent();
        if (parentReactView == null || !(parentReactView instanceof View)) {
            return 0;
        }
        return parentReactView.getWidth();
    }

    /**
     * Attempts to retrieve the web view from within the KlarnaPaymentView. This is really fragile.
     */
    private WebView getPaymentViewWebView() {
        for (int i = 0; i < paymentView.getChildCount(); i++) {
            View child = paymentView.getChildAt(i);

            if (child instanceof WebView) {
                return (WebView) child;
            }
        }
        return null;
    }

    /**
     * Returns the app context the wrapper was initialized with.
     */
    private ReactApplicationContext getReactAppContext() {
        return (ReactApplicationContext) getContext();
    }

    @Override
    public void onNewHeight(String value) {
        setHeight(value);
    }
}
