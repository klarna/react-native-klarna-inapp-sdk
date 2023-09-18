package com.klarna.mobile.sdk.reactnative.payments;

import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.LinearLayout;

import com.facebook.react.bridge.GuardedRunnable;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.uimanager.PixelUtil;
import com.facebook.react.uimanager.UIManagerModule;
import com.klarna.mobile.sdk.api.payments.KlarnaPaymentView;
import com.klarna.mobile.sdk.api.payments.KlarnaPaymentViewCallback;
import com.klarna.mobile.sdk.reactnative.common.WebViewResizeObserver;

/***
 * Wraps the KlarnaPaymentView so we can see when a requestLayout() has been triggered.
 */
public class PaymentViewWrapper extends LinearLayout implements WebViewResizeObserver.WebViewResizeObserverCallback {

    public interface OnResizedListener {
        void onResized(PaymentViewWrapper paymentViewWrapper, int value);
    }

    public KlarnaPaymentView paymentView;
    private final WebViewResizeObserver resizeObserver;

    private final OnResizedListener onResizedListener;

    public PaymentViewWrapper(ReactApplicationContext context, AttributeSet attrs, KlarnaPaymentViewCallback paymentViewCallback, OnResizedListener onResizedListener) {
        super(context, attrs);
        this.onResizedListener = onResizedListener;
        // Add KlarnaPaymentView
        ViewGroup.LayoutParams webViewParams = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        paymentView = new KlarnaPaymentView(getReactAppContext().getCurrentActivity(), attrs); // Insure we use activity and not application context for dialogs.
        paymentView.registerPaymentViewCallback(paymentViewCallback);
        addView(paymentView, webViewParams);
        resizeObserver = new WebViewResizeObserver(this);
        WebView webView = getPaymentViewWebView();
        if (webView != null) {
            resizeObserver.addInterface(webView);
        }
    }

    public void load(String sessionData) {
        paymentView.load(sessionData);
        WebView webView = getPaymentViewWebView();
        if (webView != null) {
            resizeObserver.injectListener(webView);
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
    private void setHeight(int height) {
        try {
            final int width = getParentViewWidth();
            final int scaledHeight = (int) PixelUtil.toPixelFromDIP(height);
            getReactAppContext().runOnNativeModulesQueueThread(new GuardedRunnable(getReactAppContext()) {
                @Override
                public void runGuarded() {
                    UIManagerModule uimm = getReactAppContext().getNativeModule(UIManagerModule.class);
                    if (uimm != null) {
                        uimm.updateNodeSize(getId(), width, scaledHeight);
                    }
                }
            });
        } catch (Throwable t) {
            // ignore
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
    public void onResized(int value) {
        if (onResizedListener != null) {
            onResizedListener.onResized(this, value);
        }
        setHeight(value);
    }
}
