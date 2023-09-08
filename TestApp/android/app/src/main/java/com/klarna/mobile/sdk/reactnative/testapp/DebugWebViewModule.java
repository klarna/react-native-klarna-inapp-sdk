package com.klarna.mobile.sdk.reactnative.testapp;

import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.webkit.WebView;

import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;

import javax.annotation.Nonnull;

public class DebugWebViewModule extends ReactContextBaseJavaModule {

    private static ReactApplicationContext reactContext;

    DebugWebViewModule(ReactApplicationContext context) {
        super(context);
        reactContext = context;
    }

    @Nonnull
    @Override
    public String getName() {
        return "DebugWebViewModule";
    }

    @ReactMethod
    public void enable() {
        new Handler(Looper.getMainLooper()).post(() -> {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                WebView.setWebContentsDebuggingEnabled(true);
            }
        });
    }

    @ReactMethod
    public void disable() {
        new Handler(Looper.getMainLooper()).post(() -> {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                WebView.setWebContentsDebuggingEnabled(false);
            }
        });
    }
}
