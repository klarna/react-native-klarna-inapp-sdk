package com.klarna.inapp.sdk;

import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.facebook.react.bridge.ReadableArray;
import com.facebook.react.uimanager.SimpleViewManager;
import com.facebook.react.uimanager.ViewManagerDelegate;
import com.facebook.react.viewmanagers.RNKlarnaPaymentViewManagerDelegate;
import com.facebook.react.viewmanagers.RNKlarnaPaymentViewManagerInterface;

public abstract class RNKlarnaPaymentViewSpec<T extends View> extends SimpleViewManager<T> implements RNKlarnaPaymentViewManagerInterface<T> {
  private final ViewManagerDelegate<T> mDelegate;

  public RNKlarnaPaymentViewSpec() {
    mDelegate = new RNKlarnaPaymentViewManagerDelegate<>(this);
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
