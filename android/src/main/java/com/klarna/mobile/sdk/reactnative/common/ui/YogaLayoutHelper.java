package com.klarna.mobile.sdk.reactnative.common.ui;

import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;

/**
 * Shared layout helper for native child views hosted inside React Native's Yoga layout engine.
 *
 * <p>Yoga suppresses {@code requestLayout()} from native child views, so SDK-provided views
 * (e.g. KlarnaExpressCheckoutButton, KlarnaOSMView) won't measure or layout correctly without
 * a manual pass. Both the Express Checkout and OSM view managers use this helper to perform
 * that manual measure+layout.
 */
public final class YogaLayoutHelper {

    private YogaLayoutHelper() {
    }

    /**
     * Measures and lays out a child view to fill the wrapper's width while using its own
     * intrinsic height.
     *
     * @param wrapper the parent container whose width determines the child's width
     * @param child   the native child view to measure and lay out
     */
    public static void measureAndLayout(@NonNull ViewGroup wrapper, @NonNull View child) {
        int width = wrapper.getMeasuredWidth();
        if (width <= 0) {
            return;
        }
        child.measure(
                View.MeasureSpec.makeMeasureSpec(width, View.MeasureSpec.EXACTLY),
                View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED)
        );
        child.layout(0, 0, width, child.getMeasuredHeight());
    }
}
