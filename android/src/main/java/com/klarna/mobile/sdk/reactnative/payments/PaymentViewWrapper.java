package com.klarna.mobile.sdk.reactnative.payments;

import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.LinearLayout;

import com.facebook.react.bridge.ReactApplicationContext;
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
        ViewGroup.LayoutParams webViewParams = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        paymentView = new KlarnaPaymentView(getReactAppContext().getCurrentActivity(), attrs); // Insure we use activity and not application context for dialogs.
        paymentView.registerPaymentViewCallback(paymentViewCallback);
        addView(paymentView, webViewParams);
        resizeObserver = new WebViewResizeObserver(this, WebViewResizeObserver.TargetElement.PAYMENT_CONTAINER);
        WebView webView = getPaymentViewWebView();
        if (webView != null) {
            resizeObserver.addJavascriptInterface(webView);
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
    }
}
