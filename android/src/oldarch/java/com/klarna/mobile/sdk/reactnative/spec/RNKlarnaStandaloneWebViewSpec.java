package com.klarna.mobile.sdk.reactnative.spec;

import android.view.View;

import androidx.annotation.Nullable;

import com.facebook.react.uimanager.SimpleViewManager;

public abstract class RNKlarnaStandaloneWebViewSpec<T extends View> extends SimpleViewManager<T> {
    public abstract void setReturnUrl(T view, @Nullable String returnUrl);
}
