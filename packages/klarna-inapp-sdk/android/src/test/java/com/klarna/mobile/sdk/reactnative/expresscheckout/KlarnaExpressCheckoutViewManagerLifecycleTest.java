package com.klarna.mobile.sdk.reactnative.expresscheckout;

import android.app.Activity;
import android.view.View;
import android.widget.FrameLayout;

import com.facebook.react.bridge.Arguments;
import com.facebook.react.bridge.JavaOnlyArray;
import com.facebook.react.bridge.JavaOnlyMap;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.uimanager.ThemedReactContext;
import com.facebook.react.uimanager.UIManagerHelper;
import com.facebook.react.uimanager.events.EventDispatcher;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.shadows.ShadowLooper;

import java.util.concurrent.TimeUnit;

/**
 * Covers the tab-switch lifecycle: the button is torn down when the wrapper detaches and has to
 * be rebuilt when it attaches again, and late height changes of the button are re-measured.
 */
@RunWith(RobolectricTestRunner.class)
public class KlarnaExpressCheckoutViewManagerLifecycleTest {

    private static final int WRAPPER_WIDTH_PX = 400;
    private static final int WRAPPER_HEIGHT_PX = 48;
    private static final int BUTTON_HEIGHT_PX = 120;

    private MockedStatic<UIManagerHelper> mockedUiManagerHelper;
    private MockedStatic<Arguments> mockedArguments;
    private EventDispatcher dispatcher;
    private Activity activity;
    private KlarnaExpressCheckoutViewManager manager;
    private FrameLayout wrapper;

    @Before
    public void setup() {
        // Under Robolectric the SDK button cannot be built, so the creation-error path runs and
        // needs Arguments, whose native-backed implementation is unavailable.
        mockedArguments = Mockito.mockStatic(Arguments.class);
        mockedArguments.when(Arguments::createArray).thenAnswer(invocation -> new JavaOnlyArray());
        mockedArguments.when(Arguments::createMap).thenAnswer(invocation -> new JavaOnlyMap());

        dispatcher = Mockito.mock(EventDispatcher.class);
        mockedUiManagerHelper = Mockito.mockStatic(UIManagerHelper.class);
        mockedUiManagerHelper
                .when(() -> UIManagerHelper.getEventDispatcherForReactTag(Mockito.any(), Mockito.anyInt()))
                .thenReturn(dispatcher);
        mockedUiManagerHelper
                .when(() -> UIManagerHelper.getSurfaceId(Mockito.any(View.class)))
                .thenReturn(1);

        activity = Robolectric.buildActivity(Activity.class).setup().get();
        ReactApplicationContext reactContext = Mockito.mock(
                ReactApplicationContext.class,
                Mockito.withSettings().useConstructor(activity).defaultAnswer(Mockito.CALLS_REAL_METHODS));
        ThemedReactContext themedContext = new ThemedReactContext(reactContext, activity, null, -1);

        manager = new KlarnaExpressCheckoutViewManager();
        wrapper = manager.createViewInstance(themedContext);
    }

    @After
    public void teardown() {
        mockedUiManagerHelper.close();
        mockedArguments.close();
    }

    private void attachToWindow() {
        activity.setContentView(wrapper, new FrameLayout.LayoutParams(WRAPPER_WIDTH_PX, WRAPPER_HEIGHT_PX));
        idleFrames();
    }

    private void detachFromWindow() {
        ((FrameLayout) wrapper.getParent()).removeView(wrapper);
        idleFrames();
    }

    private void idleFrames() {
        ShadowLooper.idleMainLooper(100, TimeUnit.MILLISECONDS);
    }

    private void deliverProps() {
        manager.setSessionType(wrapper, "clientId");
        manager.setClientId(wrapper, "client-id");
        manager.setLocale(wrapper, "en-US");
        manager.setEnvironment(wrapper, "playground");
        manager.setRegion(wrapper, "eu");
        manager.setReturnUrl(wrapper, "app://return");
    }

    /**
     * Stands in for the SDK button: wants {@link #BUTTON_HEIGHT_PX} of content height but, like a
     * real view, is clipped when a normal (non-Yoga) measure pass bounds it to the wrapper's height.
     */
    private View addFakeButton() {
        View button = new View(activity) {
            @Override
            protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
                setMeasuredDimension(
                        MeasureSpec.getSize(widthMeasureSpec),
                        resolveSize(BUTTON_HEIGHT_PX, heightMeasureSpec));
            }
        };
        wrapper.addView(button, new FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.WRAP_CONTENT));
        return button;
    }

    @Test
    public void detachTearsDownTheButton() {
        deliverProps();
        attachToWindow();
        addFakeButton();
        Assert.assertEquals(1, wrapper.getChildCount());

        detachFromWindow();

        Assert.assertEquals(0, wrapper.getChildCount());
    }

    @Test
    public void attachWithoutDeliveredPropsDoesNotBuildAButton() {
        attachToWindow();

        Assert.assertEquals(0, wrapper.getChildCount());
        Mockito.verifyNoInteractions(dispatcher);
    }

    @Test
    public void attachAfterDetachSchedulesButtonRecreation() {
        deliverProps();
        attachToWindow();
        detachFromWindow();
        Assert.assertEquals(0, wrapper.getChildCount());
        // The initial creation and the teardown already ran: forget what the first pass did.
        Mockito.clearInvocations(dispatcher);

        attachToWindow();

        // createButton() runs on the next frame. Whether the SDK button itself can be built under
        // Robolectric or reports a creation error, a rebuild attempt must have happened, which is
        // visible as the wrapper having been repopulated or an error event having been dispatched.
        boolean rebuilt = wrapper.getChildCount() == 1;
        boolean errorReported = !Mockito.mockingDetails(dispatcher).getInvocations().isEmpty();
        Assert.assertTrue("Expected a rebuild attempt after re-attach", rebuilt || errorReported);
    }

    @Test
    public void relayoutGrowsTheButtonToItsCurrentContentHeight() {
        deliverProps();
        attachToWindow();
        View button = addFakeButton();

        manager.relayout(wrapper);

        Assert.assertEquals(BUTTON_HEIGHT_PX, button.getHeight());
        Assert.assertEquals(WRAPPER_WIDTH_PX, button.getWidth());
    }

    @Test
    public void relayoutIsANoOpWithoutAButton() {
        deliverProps();
        attachToWindow();
        wrapper.removeAllViews();

        manager.relayout(wrapper);

        Assert.assertEquals(0, wrapper.getChildCount());
    }

    @Test
    public void relayoutIsANoOpWhenDetached() {
        View button = addFakeButton();

        manager.relayout(wrapper);

        Assert.assertEquals(0, button.getHeight());
    }

    @Test
    public void layoutRequestFromTheButtonTriggersARelayout() {
        deliverProps();
        attachToWindow();
        View button = addFakeButton();
        wrapper.measure(
                View.MeasureSpec.makeMeasureSpec(WRAPPER_WIDTH_PX, View.MeasureSpec.EXACTLY),
                View.MeasureSpec.makeMeasureSpec(WRAPPER_HEIGHT_PX, View.MeasureSpec.EXACTLY));
        wrapper.layout(0, 0, WRAPPER_WIDTH_PX, WRAPPER_HEIGHT_PX);
        Assert.assertEquals("Button is clipped to the wrapper before the request",
                WRAPPER_HEIGHT_PX, button.getHeight());

        // What the SDK button does when its label finishes loading.
        button.requestLayout();
        idleFrames();

        Assert.assertEquals(BUTTON_HEIGHT_PX, button.getHeight());
    }
}
