package com.klarna.mobile.sdk.reactnative.spec;

import android.view.View;

import androidx.annotation.Nullable;

import com.facebook.react.uimanager.SimpleViewManager;

public abstract class RNKlarnaOSMViewSpec<T extends View> extends SimpleViewManager<T> {

    public abstract void setClientId(T view, @Nullable String value);

    public abstract void setPlacementKey(T view, @Nullable String value);

    public abstract void setLocale(T view, @Nullable String value);

    public abstract void setPurchaseAmount(T view, @Nullable String value);

    public abstract void setEnvironment(T view, @Nullable String value);

    public abstract void setRegion(T view, @Nullable String value);

    public abstract void setTheme(T view, @Nullable String value);

    public abstract void setBackgroundColor(T view, @Nullable String value);

    public abstract void setTextColor(T view, @Nullable String value);

    public abstract void setReturnUrl(T view, @Nullable String value);

    public abstract void render(T view);
}
