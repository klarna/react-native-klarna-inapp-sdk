package com.klarna.mobile.sdk.reactnative.spec;

import android.view.View;

import androidx.annotation.Nullable;

import com.facebook.react.uimanager.SimpleViewManager;

public abstract class RNKlarnaPaymentViewSpec<T extends View> extends SimpleViewManager<T> {
    public abstract void setCategory(T view, @Nullable String value);

    public abstract void setReturnUrl(T view, @Nullable String value);

    public abstract void initialize(T view, String clientToken, String returnUrl);

    public abstract void load(T view, String sessionData);

    public abstract void loadPaymentReview(T view);

    public abstract void authorize(T view, boolean autoFinalize, String sessionData);

    public abstract void reauthorize(T view, String sessionData);

    public abstract void finalize(T view, String sessionData);
}
