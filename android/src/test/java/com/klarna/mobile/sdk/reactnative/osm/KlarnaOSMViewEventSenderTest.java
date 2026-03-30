package com.klarna.mobile.sdk.reactnative.osm;

import android.os.SystemClock;
import android.view.View;

import com.facebook.react.bridge.Arguments;
import com.facebook.react.bridge.JavaOnlyArray;
import com.facebook.react.bridge.JavaOnlyMap;
import com.facebook.react.bridge.ReactContext;
import com.facebook.react.bridge.WritableMap;
import com.facebook.react.uimanager.UIManagerHelper;
import com.facebook.react.uimanager.events.EventDispatcher;
import com.klarna.mobile.sdk.KlarnaMobileSDKError;
import com.klarna.mobile.sdk.api.osm.KlarnaOSMView;
import com.klarna.mobile.sdk.reactnative.common.event.ComponentEvent;
import com.klarna.mobile.sdk.reactnative.common.ui.WrapperView;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@RunWith(MockitoJUnitRunner.class)
public class KlarnaOSMViewEventSenderTest {

    private MockedStatic<UIManagerHelper> mockedStaticUiManagerHelper;
    private MockedStatic<SystemClock> mockedStaticSystemClock;
    private MockedStatic<Arguments> mockedStaticArguments;
    private KlarnaOSMViewEventSender eventSender;
    private WrapperView<KlarnaOSMView> wrapper;
    private KlarnaOSMView osmView;
    private EventDispatcher eventDispatcher;
    private Map<WrapperView<KlarnaOSMView>, EventDispatcher> viewToDispatcher;

    @SuppressWarnings("unchecked")
    @Before
    public void setup() {
        osmView = Mockito.mock(KlarnaOSMView.class);
        wrapper = Mockito.mock(WrapperView.class);
        Mockito.when(wrapper.getView()).thenReturn(osmView);

        ReactContext reactContext = Mockito.mock(ReactContext.class);
        Mockito.when(wrapper.getContext()).thenReturn(reactContext);

        eventDispatcher = Mockito.mock(EventDispatcher.class);
        viewToDispatcher = new HashMap<>();
        viewToDispatcher.put(wrapper, eventDispatcher);

        eventSender = new KlarnaOSMViewEventSender(viewToDispatcher);

        mockedStaticUiManagerHelper = Mockito.mockStatic(UIManagerHelper.class);
        Mockito.when(UIManagerHelper.getEventDispatcherForReactTag(Mockito.any(), Mockito.anyInt()))
                .thenAnswer(invocation -> eventDispatcher);
        mockedStaticSystemClock = Mockito.mockStatic(SystemClock.class);
        mockedStaticArguments = Mockito.mockStatic(Arguments.class);
        Mockito.when(Arguments.createArray())
                .thenAnswer(invocation -> new JavaOnlyArray());
        Mockito.when(Arguments.createMap())
                .thenAnswer(invocation -> new JavaOnlyMap());
    }

    @After
    public void teardown() {
        mockedStaticUiManagerHelper.close();
        mockedStaticSystemClock.close();
        mockedStaticArguments.close();
    }

    // --- getCallbackEventNames ---

    @Test
    public void testCallbackEventNames() {
        Collection<String> names = eventSender.getCallbackEventNames();
        Assert.assertEquals(3, names.size());
        Assert.assertTrue(names.contains("onError"));
        Assert.assertTrue(names.contains("onResized"));
        Assert.assertTrue(names.contains("onOSMViewReady"));
    }

    // --- getExportedCustomDirectEventTypeConstants ---

    @Test
    public void testExportedCustomDirectEventTypeConstants() {
        Map<String, Object> constants = eventSender.getExportedCustomDirectEventTypeConstants();
        Assert.assertEquals(3, constants.size());
        Assert.assertTrue(constants.containsKey("onError"));
        Assert.assertTrue(constants.containsKey("onResized"));
        Assert.assertTrue(constants.containsKey("onOSMViewReady"));

        for (String key : constants.keySet()) {
            @SuppressWarnings("unchecked")
            Map<String, String> value = (Map<String, String>) constants.get(key);
            Assert.assertNotNull(value);
            Assert.assertEquals(key, value.get("registrationName"));
        }
    }

    // --- sendError ---

    @Test
    public void testSendError() {
        eventSender.sendError(osmView, "TestError", "Test message", false);
        Mockito.verify(eventDispatcher).dispatchEvent(Mockito.any(ComponentEvent.class));
    }

    @Test
    public void testSendErrorFatal() {
        eventSender.sendError(osmView, "FatalError", "Fatal message", true);
        Mockito.verify(eventDispatcher).dispatchEvent(Mockito.any(ComponentEvent.class));
    }

    // --- sendKlarnaMobileSDKError ---

    @Test
    public void testSendKlarnaMobileSDKError() {
        KlarnaMobileSDKError error = Mockito.mock(KlarnaMobileSDKError.class);
        Mockito.when(error.getName()).thenReturn("SDKError");
        Mockito.when(error.getMessage()).thenReturn("SDK error message");
        Mockito.when(error.isFatal()).thenReturn(true);

        eventSender.sendKlarnaMobileSDKError(osmView, error);
        Mockito.verify(eventDispatcher).dispatchEvent(Mockito.any(ComponentEvent.class));
    }

    // --- sendOnResizedEvent ---

    @Test
    public void testSendOnResizedEvent() {
        eventSender.sendOnResizedEvent(osmView, 150);
        Mockito.verify(eventDispatcher).dispatchEvent(Mockito.any(ComponentEvent.class));
    }

    @Test
    public void testSendOnResizedEventZeroHeight() {
        eventSender.sendOnResizedEvent(osmView, 0);
        Mockito.verify(eventDispatcher).dispatchEvent(Mockito.any(ComponentEvent.class));
    }

    // --- sendOnOSMViewReadyEvent ---

    @Test
    public void testSendOnOSMViewReadyEvent() {
        eventSender.sendOnOSMViewReadyEvent(osmView);
        Mockito.verify(eventDispatcher).dispatchEvent(Mockito.any(ComponentEvent.class));
    }

    // --- null view handling ---

    @Test
    public void testSendErrorNullView() {
        eventSender.sendError(null, "TestError", "Test message", false);
        Mockito.verify(eventDispatcher, Mockito.never()).dispatchEvent(Mockito.any());
    }

    @Test
    public void testSendOnResizedEventNullView() {
        eventSender.sendOnResizedEvent(null, 100);
        Mockito.verify(eventDispatcher, Mockito.never()).dispatchEvent(Mockito.any());
    }

    @Test
    public void testSendOnOSMViewReadyEventNullView() {
        eventSender.sendOnOSMViewReadyEvent(null);
        Mockito.verify(eventDispatcher, Mockito.never()).dispatchEvent(Mockito.any());
    }

    // --- view not in dispatcher map ---

    @Test
    public void testSendErrorUnknownView() {
        KlarnaOSMView unknownView = Mockito.mock(KlarnaOSMView.class);
        eventSender.sendError(unknownView, "TestError", "Test message", false);
        Mockito.verify(eventDispatcher, Mockito.never()).dispatchEvent(Mockito.any());
    }
}
