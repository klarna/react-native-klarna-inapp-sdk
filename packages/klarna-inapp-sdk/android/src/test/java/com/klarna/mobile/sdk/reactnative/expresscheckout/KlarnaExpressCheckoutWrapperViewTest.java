package com.klarna.mobile.sdk.reactnative.expresscheckout;

import android.app.Activity;
import android.widget.FrameLayout;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.shadows.ShadowLooper;

import java.util.concurrent.atomic.AtomicInteger;

@RunWith(RobolectricTestRunner.class)
public class KlarnaExpressCheckoutWrapperViewTest {

    private Activity activity;
    private KlarnaExpressCheckoutWrapperView wrapper;
    private AtomicInteger relayoutCount;

    @Before
    public void setup() {
        activity = Robolectric.buildActivity(Activity.class).setup().get();
        wrapper = new KlarnaExpressCheckoutWrapperView(activity);
        relayoutCount = new AtomicInteger();
        wrapper.setRelayoutListener(view -> relayoutCount.incrementAndGet());
    }

    private void attach() {
        activity.setContentView(wrapper, new FrameLayout.LayoutParams(400, 100));
        ShadowLooper.idleMainLooper();
        // Attaching itself requests layout; start every test from a clean count.
        relayoutCount.set(0);
    }

    @Test
    public void requestLayoutNotifiesListenerOnce() {
        attach();

        wrapper.requestLayout();
        ShadowLooper.idleMainLooper();

        Assert.assertEquals(1, relayoutCount.get());
    }

    @Test
    public void multipleRequestLayoutCallsCoalesceIntoOneCallback() {
        attach();

        wrapper.requestLayout();
        wrapper.requestLayout();
        wrapper.requestLayout();
        ShadowLooper.idleMainLooper();

        Assert.assertEquals(1, relayoutCount.get());
    }

    @Test
    public void requestLayoutAfterCallbackSchedulesAnotherPass() {
        attach();

        wrapper.requestLayout();
        ShadowLooper.idleMainLooper();
        wrapper.requestLayout();
        ShadowLooper.idleMainLooper();

        Assert.assertEquals(2, relayoutCount.get());
    }

    @Test
    public void requestLayoutWhileDetachedDoesNotNotifyListener() {
        // Never attached to a window.
        wrapper.requestLayout();
        ShadowLooper.idleMainLooper();

        Assert.assertEquals(0, relayoutCount.get());
    }

    @Test
    public void requestLayoutInsideCallbackDoesNotReschedule() {
        attach();
        wrapper.setRelayoutListener(view -> {
            relayoutCount.incrementAndGet();
            // A button that requests layout from onMeasure must not cause an endless loop.
            view.requestLayout();
        });

        wrapper.requestLayout();
        ShadowLooper.idleMainLooper();

        Assert.assertEquals(1, relayoutCount.get());
    }

    @Test
    public void requestPendingWhenDetachedDoesNotBlockLaterRequests() {
        attach();
        wrapper.requestLayout();

        // Detach before the posted callback gets to run.
        ((FrameLayout) wrapper.getParent()).removeView(wrapper);
        ShadowLooper.idleMainLooper();
        Assert.assertEquals("No callback while detached", 0, relayoutCount.get());

        attach();
        wrapper.requestLayout();
        ShadowLooper.idleMainLooper();

        Assert.assertEquals(1, relayoutCount.get());
    }

    @Test
    public void requestLayoutWithoutListenerIsIgnored() {
        attach();
        wrapper.setRelayoutListener(null);

        wrapper.requestLayout();
        ShadowLooper.idleMainLooper();

        Assert.assertEquals(0, relayoutCount.get());
    }
}
