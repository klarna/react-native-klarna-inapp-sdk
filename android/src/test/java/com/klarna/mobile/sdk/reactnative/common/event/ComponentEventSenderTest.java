package com.klarna.mobile.sdk.reactnative.common.event;

import android.os.SystemClock;
import android.view.View;

import com.facebook.react.bridge.WritableMap;
import com.facebook.react.uimanager.UIManagerHelper;
import com.facebook.react.uimanager.events.EventDispatcher;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

import java.lang.ref.WeakReference;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@RunWith(MockitoJUnitRunner.class)
public class ComponentEventSenderTest {

    private MockedStatic<UIManagerHelper> mockedStaticUiManagerHelper;
    private MockedStatic<SystemClock> mockedStaticSystemClock;
    private TestEventSender testEventSender;
    private View view;
    private EventDispatcher eventDispatcher;
    private Map<WeakReference<View>, EventDispatcher> viewToDispatcher;

    @Before
    public void setUp() {
        view = Mockito.mock(View.class);
        eventDispatcher = Mockito.mock(EventDispatcher.class);
        viewToDispatcher = new HashMap<>() {{
            put(new WeakReference<>(view), eventDispatcher);
        }};
        testEventSender = new TestEventSender(viewToDispatcher);

        mockedStaticUiManagerHelper = Mockito.mockStatic(UIManagerHelper.class);
        Mockito.when(UIManagerHelper.getEventDispatcherForReactTag(Mockito.any(), Mockito.anyInt())).thenAnswer(invocation -> eventDispatcher);
        mockedStaticSystemClock = Mockito.mockStatic(SystemClock.class);
    }

    @After
    public void teardown() {
        mockedStaticUiManagerHelper.close();
        mockedStaticSystemClock.close();
    }

    @Test
    public void exportedCustomDirectEventTypeConstants() {
        // When
        Map<String, Object> customDirectEventTypeConstants = testEventSender.getExportedCustomDirectEventTypeConstants();

        // Then
        Assert.assertEquals(1, customDirectEventTypeConstants.size());
        Assert.assertTrue(customDirectEventTypeConstants.containsKey("onTestEvent"));
        Assert.assertEquals("onTestEvent", ((Map<String, String>) customDirectEventTypeConstants.get("onTestEvent")).get("registrationName"));
    }

    @Test
    public void testPostTestEventForView() {
        // Given
        WritableMap params = null;

        // When
        testEventSender.postTestEventForView(view, params);

        // Then
        Mockito.verify(eventDispatcher).dispatchEvent(Mockito.any(ComponentEvent.class));
    }

    private class TestEventSender extends ComponentEventSender<View> {
        public TestEventSender(Map<WeakReference<View>, EventDispatcher> viewToDispatcher) {
            super(viewToDispatcher);
        }

        @Override
        public Collection<String> getCallbackEventNames() {
            return Arrays.asList("onTestEvent");
        }

        public void postTestEventForView(View view, WritableMap params) {
            postEventForView(view, "onTestEvent", params);
        }
    }
}
