package com.klarna.inapp.sdk;

import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.facebook.react.bridge.Arguments;
import com.facebook.react.bridge.ReactContext;
import com.facebook.react.bridge.ReadableArray;
import com.facebook.react.bridge.WritableMap;
import com.facebook.react.uimanager.SimpleViewManager;
import com.facebook.react.uimanager.UIManagerHelper;
import com.facebook.react.uimanager.ViewManagerDelegate;
import com.facebook.react.viewmanagers.RNKlarnaPaymentViewManagerDelegate;
import com.facebook.react.viewmanagers.RNKlarnaPaymentViewManagerInterface;
import com.facebook.react.views.view.ReactViewGroup;
import com.klarna.mobile.sdk.api.payments.KlarnaPaymentView;

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

  public void updateNodeSize(int height, int width, int nodeId, ReactContext context){
    WritableMap params = Arguments.createMap();
    params.putInt("height", height);
    KlarnaPaymentEvent event = new KlarnaPaymentEvent(nodeId, KlarnaPaymentEvent.EVENT_TYPE_NAME_ON_WEBVIEW_HEIGHT_CHANGE, params);
    UIManagerHelper.getEventDispatcherForReactTag(context, nodeId).dispatchEvent(event);
  }

}
