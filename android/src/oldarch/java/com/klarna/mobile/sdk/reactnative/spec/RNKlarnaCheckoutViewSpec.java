package com.klarna.mobile.sdk.reactnative.spec;

import android.view.View;

import androidx.annotation.Nullable;

import com.facebook.react.bridge.ReadableMap;
import com.facebook.react.uimanager.SimpleViewManager;
import com.klarna.mobile.sdk.api.checkout.KlarnaCheckoutView;

public abstract class RNKlarnaCheckoutViewSpec<T extends View> extends SimpleViewManager<T> {

    public abstract void setReturnUrl(KlarnaCheckoutView view, @Nullable String value);

    public abstract void setCheckoutOptions(KlarnaCheckoutView view, @Nullable ReadableMap value);

    public abstract void setSnippet(KlarnaCheckoutView view, String snippet);

    public abstract void suspend(KlarnaCheckoutView view);

    public abstract void resume(KlarnaCheckoutView view);
}
