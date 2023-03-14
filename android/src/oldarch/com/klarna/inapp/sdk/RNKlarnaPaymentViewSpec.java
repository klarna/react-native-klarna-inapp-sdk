package com.klarna.inapp.sdk;

import android.view.View;

import androidx.annotation.Nullable;

import com.facebook.react.bridge.GuardedRunnable;
import com.facebook.react.bridge.ReactContext;
import com.facebook.react.uimanager.SimpleViewManager;
import com.facebook.react.uimanager.UIManagerModule;

public abstract class RNKlarnaPaymentViewSpec<T extends View> extends SimpleViewManager<T> {
    abstract void setCategory(T view, @Nullable String value);
    abstract void initialize(T view, String clientToken, String returnUrl);
    abstract void load(T view, String sessionData);
    abstract void loadPaymentReview(T view);
    abstract void authorize(T view, boolean autoFinalize, String sessionData);
    abstract void reauthorize(T view, String sessionData);
    abstract void finalize(T view, String sessionData);
    abstract void initialize(PaymentViewWrapper view, String clientToken, String returnUrl);
    abstract void load(PaymentViewWrapper view, String sessionData);
    abstract void loadPaymentReview(PaymentViewWrapper view);
    abstract void authorize(PaymentViewWrapper view, boolean autoFinalize, String sessionData);
    abstract void reauthorize(PaymentViewWrapper view, String sessionData);
    abstract void finalize(PaymentViewWrapper view, String sessionData);

    public void updateNodeSize(int height, int width, int nodeId, ReactContext context){
        float scaledHeight = context.getResources().getDisplayMetrics().density * height;
        context.runOnNativeModulesQueueThread(new GuardedRunnable(context) {
            @Override
            public void runGuarded() {
                UIManagerModule uimm = context.getNativeModule(UIManagerModule.class);
                uimm.updateNodeSize(nodeId, width, (int) scaledHeight);
            }
        });
    }
}
