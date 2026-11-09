package com.klarna.mobile.sdk.reactnative.spec;

import android.view.View;

import androidx.annotation.Nullable;

import com.facebook.react.uimanager.SimpleViewManager;

public abstract class RNKlarnaStandaloneWebViewSpec<T extends View> extends SimpleViewManager<T> {
    public abstract void setReturnUrl(T view, @Nullable String returnUrl);

    public abstract void setTheme(T view, @Nullable String value);

    public abstract void setOverScrollMode(T view, String overScrollMode);

    public abstract void setBounces(T view, boolean bounces);

    public abstract void setSharedCookiesEnabled(T view, boolean sharedCookiesEnabled);

    public abstract void load(T view, String url, @Nullable String optionsJson);

    public abstract void goForward(T view);

    public abstract void goBack(T view);

    public abstract void reload(T view);

    public abstract void stopLoading(T view);

    public abstract void setCookies(T view, int requestId, String cookiesJson);
}
