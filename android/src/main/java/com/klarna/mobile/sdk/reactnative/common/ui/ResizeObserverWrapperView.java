package com.klarna.mobile.sdk.reactnative.common.ui;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.facebook.react.bridge.ReactApplicationContext;
import com.klarna.mobile.sdk.reactnative.common.WebViewResizeObserver;

import java.lang.ref.WeakReference;

public class ResizeObserverWrapperView<T extends ViewGroup> extends WrapperView<T> {

    public interface OnResizedListener<T extends ViewGroup> {
        void onResized(ResizeObserverWrapperView<T> resizeObserverWrapperView, int newHeight);
    }

    private WebViewResizeObserver webViewResizeObserver;
    private WebViewResizeObserver.WebViewResizeObserverCallback webViewResizeObserverCallback;
    private OnResizedListener<T> onResizedListener;

    public ResizeObserverWrapperView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public ResizeObserverWrapperView(ReactApplicationContext context, AttributeSet attrs, T view) {
        super(context, attrs, view);
    }

    public void initiateWebViewResizeObserver(WebViewResizeObserver.TargetElement targetElement, OnResizedListener<T> onResizedListener) {
        this.onResizedListener = onResizedListener;
        this.webViewResizeObserverCallback = new WebViewResizeObserver.WebViewResizeObserverCallback() {

            private final WeakReference<OnResizedListener<T>> onResizedListenerWeakReference = new WeakReference<>(ResizeObserverWrapperView.this.onResizedListener);

            @Override
            public void onResized(int value) {
                OnResizedListener<T> listener = onResizedListenerWeakReference.get();
                if (listener != null) {
                    listener.onResized(ResizeObserverWrapperView.this, value);
                }
            }
        };
        this.webViewResizeObserver = new WebViewResizeObserver(webViewResizeObserverCallback, targetElement);
    }

    public void addInterfaceToWebView() {
        WebView webView = findWebView(getView());
        if (webView != null && webViewResizeObserver != null) {
            webViewResizeObserver.addInterface(webView);
        }
    }

    public void injectListenerToWebView() {
        WebView webView = findWebView(getView());
        if (webView != null && webViewResizeObserver != null) {
            webViewResizeObserver.injectListener(webView);
        }
    }

    private WebView findWebView(T view) {
        if (view == null) {
            return null;
        }
        for (int i = 0; i < view.getChildCount(); i++) {
            View child = view.getChildAt(i);

            if (child instanceof WebView) {
                return (WebView) child;
            }
        }
        return null;
    }
}
