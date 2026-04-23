package com.klarna.mobile.sdk.reactnative.osm;

import android.os.SystemClock;

import com.facebook.react.bridge.Arguments;
import com.facebook.react.bridge.JavaOnlyArray;
import com.facebook.react.bridge.JavaOnlyMap;
import com.facebook.react.bridge.ReactContext;
import com.facebook.react.uimanager.UIManagerHelper;
import com.facebook.react.uimanager.events.EventDispatcher;
import com.klarna.mobile.sdk.KlarnaMobileSDKError;
import com.klarna.mobile.sdk.api.KlarnaProductEvent;
import com.klarna.mobile.sdk.api.component.KlarnaComponent;
import com.klarna.mobile.sdk.api.osm.KlarnaOSMView;
import com.klarna.mobile.sdk.reactnative.common.event.ComponentEvent;
import com.klarna.mobile.sdk.reactnative.common.ui.WrapperView;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.HashMap;
import java.util.Map;

@RunWith(MockitoJUnitRunner.class)
public class KlarnaOSMViewEventHandlerTest {

    private MockedStatic<UIManagerHelper> mockedStaticUiManagerHelper;
    private MockedStatic<SystemClock> mockedStaticSystemClock;
    private MockedStatic<Arguments> mockedStaticArguments;
    private KlarnaOSMViewEventSender eventSender;
    private KlarnaOSMViewEventHandler eventHandler;
    private WrapperView<KlarnaOSMView> wrapper;
    private KlarnaOSMView osmView;
    private EventDispatcher eventDispatcher;

    @SuppressWarnings("unchecked")
    @Before
    public void setup() {
        osmView = Mockito.mock(KlarnaOSMView.class);
        wrapper = Mockito.mock(WrapperView.class);
        Mockito.when(wrapper.getView()).thenReturn(osmView);

        ReactContext reactContext = Mockito.mock(ReactContext.class);
        Mockito.when(wrapper.getContext()).thenReturn(reactContext);

        eventDispatcher = Mockito.mock(EventDispatcher.class);
        Map<WrapperView<KlarnaOSMView>, EventDispatcher> viewToDispatcher = new HashMap<>();
        viewToDispatcher.put(wrapper, eventDispatcher);

        mockedStaticUiManagerHelper = Mockito.mockStatic(UIManagerHelper.class);
        Mockito.when(UIManagerHelper.getEventDispatcherForReactTag(Mockito.any(), Mockito.anyInt()))
                .thenAnswer(invocation -> eventDispatcher);
        mockedStaticSystemClock = Mockito.mockStatic(SystemClock.class);
        mockedStaticArguments = Mockito.mockStatic(Arguments.class);
        Mockito.when(Arguments.createArray())
                .thenAnswer(invocation -> new JavaOnlyArray());
        Mockito.when(Arguments.createMap())
                .thenAnswer(invocation -> new JavaOnlyMap());

        eventSender = new KlarnaOSMViewEventSender(viewToDispatcher);
        eventHandler = new KlarnaOSMViewEventHandler(eventSender);
    }

    @After
    public void teardown() {
        mockedStaticUiManagerHelper.close();
        mockedStaticSystemClock.close();
        mockedStaticArguments.close();
    }

    // --- onError with KlarnaOSMView ---

    @Test
    public void testOnErrorWithOSMView() {
        KlarnaMobileSDKError error = Mockito.mock(KlarnaMobileSDKError.class);
        Mockito.when(error.getName()).thenReturn("RenderError");
        Mockito.when(error.getMessage()).thenReturn("Failed to render OSM");
        Mockito.when(error.isFatal()).thenReturn(false);

        eventHandler.onError(osmView, error);

        Mockito.verify(eventDispatcher).dispatchEvent(Mockito.any(ComponentEvent.class));
    }

    @Test
    public void testOnErrorWithOSMViewFatal() {
        KlarnaMobileSDKError error = Mockito.mock(KlarnaMobileSDKError.class);
        Mockito.when(error.getName()).thenReturn("FatalError");
        Mockito.when(error.getMessage()).thenReturn("Fatal SDK error");
        Mockito.when(error.isFatal()).thenReturn(true);

        eventHandler.onError(osmView, error);

        Mockito.verify(eventDispatcher).dispatchEvent(Mockito.any(ComponentEvent.class));
    }

    // --- onError with non-OSM component ---

    @Test
    public void testOnErrorWithNonOSMComponent() {
        KlarnaComponent nonOSMComponent = Mockito.mock(KlarnaComponent.class);
        KlarnaMobileSDKError error = Mockito.mock(KlarnaMobileSDKError.class);

        eventHandler.onError(nonOSMComponent, error);

        // Should not dispatch any event for non-OSM components
        Mockito.verify(eventDispatcher, Mockito.never()).dispatchEvent(Mockito.any());
    }

    // --- onEvent (no-op) ---

    @Test
    public void testOnEventIsNoOp() {
        KlarnaProductEvent productEvent = Mockito.mock(KlarnaProductEvent.class);

        eventHandler.onEvent(osmView, productEvent);

        // onEvent should be a no-op for OSM
        Mockito.verify(eventDispatcher, Mockito.never()).dispatchEvent(Mockito.any());
    }

    @Test
    public void testOnEventWithNonOSMComponent() {
        KlarnaComponent nonOSMComponent = Mockito.mock(KlarnaComponent.class);
        KlarnaProductEvent productEvent = Mockito.mock(KlarnaProductEvent.class);

        eventHandler.onEvent(nonOSMComponent, productEvent);

        Mockito.verify(eventDispatcher, Mockito.never()).dispatchEvent(Mockito.any());
    }
}
