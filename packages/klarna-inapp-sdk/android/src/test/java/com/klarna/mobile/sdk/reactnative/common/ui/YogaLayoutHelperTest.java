package com.klarna.mobile.sdk.reactnative.common.ui;

import android.view.View;
import android.view.ViewGroup;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.robolectric.RobolectricTestRunner;

@RunWith(RobolectricTestRunner.class)
public class YogaLayoutHelperTest {

    // region measureAndLayout

    @Test
    public void testMeasureAndLayout_measuresChildWithExactWidthAndUnspecifiedHeight() {
        ViewGroup wrapper = Mockito.mock(ViewGroup.class);
        View child = Mockito.mock(View.class);

        Mockito.when(wrapper.getMeasuredWidth()).thenReturn(400);
        Mockito.when(child.getMeasuredHeight()).thenReturn(120);

        YogaLayoutHelper.measureAndLayout(wrapper, child);

        Mockito.verify(child).measure(
                View.MeasureSpec.makeMeasureSpec(400, View.MeasureSpec.EXACTLY),
                View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED)
        );
        Mockito.verify(child).layout(0, 0, 400, 120);
    }

    @Test
    public void testMeasureAndLayout_skipsWhenWrapperWidthIsZero() {
        ViewGroup wrapper = Mockito.mock(ViewGroup.class);
        View child = Mockito.mock(View.class);

        Mockito.when(wrapper.getMeasuredWidth()).thenReturn(0);

        YogaLayoutHelper.measureAndLayout(wrapper, child);

        Mockito.verify(child, Mockito.never()).measure(Mockito.anyInt(), Mockito.anyInt());
        Mockito.verify(child, Mockito.never()).layout(
                Mockito.anyInt(), Mockito.anyInt(), Mockito.anyInt(), Mockito.anyInt());
    }

    @Test
    public void testMeasureAndLayout_skipsWhenWrapperWidthIsNegative() {
        ViewGroup wrapper = Mockito.mock(ViewGroup.class);
        View child = Mockito.mock(View.class);

        Mockito.when(wrapper.getMeasuredWidth()).thenReturn(-1);

        YogaLayoutHelper.measureAndLayout(wrapper, child);

        Mockito.verify(child, Mockito.never()).measure(Mockito.anyInt(), Mockito.anyInt());
        Mockito.verify(child, Mockito.never()).layout(
                Mockito.anyInt(), Mockito.anyInt(), Mockito.anyInt(), Mockito.anyInt());
    }

    @Test
    public void testMeasureAndLayout_handlesZeroChildHeight() {
        ViewGroup wrapper = Mockito.mock(ViewGroup.class);
        View child = Mockito.mock(View.class);

        Mockito.when(wrapper.getMeasuredWidth()).thenReturn(300);
        Mockito.when(child.getMeasuredHeight()).thenReturn(0);

        YogaLayoutHelper.measureAndLayout(wrapper, child);

        Mockito.verify(child).measure(
                View.MeasureSpec.makeMeasureSpec(300, View.MeasureSpec.EXACTLY),
                View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED)
        );
        Mockito.verify(child).layout(0, 0, 300, 0);
    }

    // endregion
}
