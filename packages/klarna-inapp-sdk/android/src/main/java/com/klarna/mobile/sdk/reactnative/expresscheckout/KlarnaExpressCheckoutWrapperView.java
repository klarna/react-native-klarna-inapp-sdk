package com.klarna.mobile.sdk.reactnative.expresscheckout;

import android.content.Context;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

/**
 * Wrapper hosting the {@code KlarnaExpressCheckoutButton}.
 *
 * <p>React Native sizes this view from Yoga and never measures its native children, so a
 * {@code requestLayout()} issued from inside the SDK button (e.g. when its label finishes
 * loading and its height changes) would otherwise leave the button clipped. This wrapper
 * turns such requests into a single deferred callback so the manager can re-measure the
 * button and report the new height back to React.
 */
class KlarnaExpressCheckoutWrapperView extends FrameLayout {

    interface RelayoutListener {
        void onRelayoutRequested(@NonNull KlarnaExpressCheckoutWrapperView view);
    }

    @Nullable
    private RelayoutListener relayoutListener;
    private boolean isRelayoutPending;
    private boolean isRelayoutInProgress;

    KlarnaExpressCheckoutWrapperView(@NonNull Context context) {
        super(context);
    }

    void setRelayoutListener(@Nullable RelayoutListener listener) {
        this.relayoutListener = listener;
    }

    @Override
    public void requestLayout() {
        super.requestLayout();
        // Requests raised while we are re-measuring ourselves must not schedule another pass,
        // otherwise a button that requests layout from onMeasure would loop forever.
        if (relayoutListener == null || isRelayoutPending || isRelayoutInProgress) {
            return;
        }
        isRelayoutPending = true;
        post(relayoutRunnable);
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        // Drop a callback that has not run yet and clear the flag, so a request that was pending
        // when we detached can never block the ones made after we attach again.
        removeCallbacks(relayoutRunnable);
        isRelayoutPending = false;
    }

    private final Runnable relayoutRunnable = () -> {
        isRelayoutPending = false;
        RelayoutListener listener = relayoutListener;
        if (listener == null || !isAttachedToWindow()) {
            return;
        }
        isRelayoutInProgress = true;
        try {
            listener.onRelayoutRequested(this);
        } finally {
            isRelayoutInProgress = false;
        }
    };
}
