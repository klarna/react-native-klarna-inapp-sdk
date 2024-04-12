package com.klarna.mobile.sdk.reactnative.spec;

import android.view.View;

import androidx.annotation.Nullable;

import com.facebook.react.bridge.ReadableMap;
import com.facebook.react.uimanager.SimpleViewManager;
import com.klarna.mobile.sdk.api.checkout.KlarnaCheckoutView;

public abstract class RNKlarnaCheckoutViewSpec<T extends View> extends SimpleViewManager<T> {

    public abstract void setReturnUrl(T view, @Nullable String value);

    public abstract void setCheckoutOptions(T view, @Nullable ReadableMap value);

    public abstract void setSnippet(T view, String snippet);

    public abstract void suspend(T view);

    public abstract void resume(T view);
}
