package com.klarna.mobile.sdk.reactnative.spec;

import android.view.View;

import androidx.annotation.Nullable;

import com.facebook.react.uimanager.SimpleViewManager;

public abstract class RNKlarnaExpressCheckoutViewSpec<T extends View> extends SimpleViewManager<T> {

    public abstract void setSessionType(T view, @Nullable String value);

    public abstract void setClientId(T view, @Nullable String value);

    public abstract void setClientToken(T view, @Nullable String value);

    public abstract void setLocale(T view, @Nullable String value);

    public abstract void setEnvironment(T view, @Nullable String value);

    public abstract void setRegion(T view, @Nullable String value);

    public abstract void setReturnUrl(T view, @Nullable String value);

    public abstract void setTheme(T view, @Nullable String value);

    public abstract void setShape(T view, @Nullable String value);

    public abstract void setButtonStyle(T view, @Nullable String value);

    public abstract void setAutoFinalize(T view, boolean value);

    public abstract void setCollectShippingAddress(T view, boolean value);

    public abstract void setSessionData(T view, @Nullable String value);
}
