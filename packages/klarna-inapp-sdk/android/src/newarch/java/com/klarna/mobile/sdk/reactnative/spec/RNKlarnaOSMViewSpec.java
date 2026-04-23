package com.klarna.mobile.sdk.reactnative.spec;

import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.facebook.react.bridge.ReadableArray;
import com.facebook.react.uimanager.SimpleViewManager;
import com.facebook.react.uimanager.ViewManagerDelegate;
import com.facebook.react.viewmanagers.RNKlarnaOSMViewManagerDelegate;
import com.facebook.react.viewmanagers.RNKlarnaOSMViewManagerInterface;

public abstract class RNKlarnaOSMViewSpec<T extends View> extends SimpleViewManager<T> implements RNKlarnaOSMViewManagerInterface<T> {
    private final ViewManagerDelegate<T> mDelegate;

    public RNKlarnaOSMViewSpec() {
        mDelegate = new RNKlarnaOSMViewManagerDelegate<>(this);
    }

    @Nullable
    @Override
    protected ViewManagerDelegate<T> getDelegate() {
        return mDelegate;
    }

    @Override
    public void receiveCommand(@NonNull T root, String commandId, @Nullable ReadableArray args) {
        mDelegate.receiveCommand(root, commandId, args);
    }
}
