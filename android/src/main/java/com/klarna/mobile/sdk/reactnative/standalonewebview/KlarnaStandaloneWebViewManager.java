package com.klarna.mobile.sdk.reactnative.standalonewebview;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.uimanager.ThemedReactContext;
import com.facebook.react.uimanager.annotations.ReactProp;
import com.klarna.mobile.sdk.api.standalonewebview.KlarnaStandaloneWebView;
import com.klarna.mobile.sdk.reactnative.spec.RNKlarnaStandaloneWebViewSpec;

import java.util.Objects;

public class KlarnaStandaloneWebViewManager extends RNKlarnaStandaloneWebViewSpec<KlarnaStandaloneWebView> {

    private static final String REACT_CLASS = "RNKlarnaStandaloneWebView";

    public KlarnaStandaloneWebViewManager(ReactApplicationContext reactContext, Application applicationContext) {
        super();
    }

    @ReactProp(name = "returnUrl")
    @Override
    public void setReturnUrl(KlarnaStandaloneWebView view, @Nullable String returnUrl) {
        if (!Objects.equals(returnUrl, view.getReturnURL())) {
            view.setReturnURL(returnUrl);
        }
    }

    @NonNull
    @Override
    public String getName() {
        return REACT_CLASS;
    }

    @NonNull
    @Override
    protected KlarnaStandaloneWebView createViewInstance(@NonNull ThemedReactContext themedReactContext) {
        // TODO Are we using the correct constructor for instantiating KlarnaStandaloneWebView?
        return new KlarnaStandaloneWebView(themedReactContext);
    }

}
