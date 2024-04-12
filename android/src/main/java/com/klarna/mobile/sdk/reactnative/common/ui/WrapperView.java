package com.klarna.mobile.sdk.reactnative.common.ui;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.facebook.react.bridge.ReactApplicationContext;

public class WrapperView<T extends View> extends FrameLayout {

    private T view;

    public WrapperView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public WrapperView(ReactApplicationContext context, AttributeSet attrs, T view) {
        super(context, attrs);
        this.view = view;
        ViewGroup.LayoutParams layoutParams = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        addView(view, layoutParams);
        view.setMinimumHeight(100);
    }

    public T getView() {
        return view;
    }
}
