package com.klarna.mobile.sdk.reactnative.spec;

import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.facebook.react.bridge.ReadableArray;
import com.facebook.react.uimanager.SimpleViewManager;
import com.facebook.react.uimanager.ViewManagerDelegate;
import com.facebook.react.viewmanagers.RNKlarnaStandaloneWebViewManagerDelegate;
import com.facebook.react.viewmanagers.RNKlarnaStandaloneWebViewManagerInterface;

public abstract class RNKlarnaStandaloneWebViewSpec<T extends View> extends SimpleViewManager<T> implements RNKlarnaStandaloneWebViewManagerInterface<T> {

    private final ViewManagerDelegate<T> viewManagerDelegate;

    public RNKlarnaStandaloneWebViewSpec() {
        viewManagerDelegate = new RNKlarnaStandaloneWebViewManagerDelegate<>(this);
    }

    @Nullable
    @Override
    protected ViewManagerDelegate<T> getDelegate() {
        return viewManagerDelegate;
    }

    @Override
    public void receiveCommand(@NonNull T root, String commandId, @Nullable ReadableArray args) {
        viewManagerDelegate.receiveCommand(root, commandId, args);
    }

}
