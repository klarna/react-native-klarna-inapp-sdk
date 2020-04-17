package com.klarna.inapp.sdk;

import android.os.SystemClock;

import com.facebook.react.bridge.Arguments;
import com.facebook.react.bridge.CatalystInstance;
import com.facebook.react.bridge.JavaOnlyArray;
import com.facebook.react.bridge.JavaOnlyMap;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.WritableMap;
import com.facebook.react.modules.core.ReactChoreographer;
import com.facebook.react.uimanager.UIManagerModule;
import com.facebook.react.uimanager.events.EventDispatcher;
import com.facebook.react.uimanager.events.EventDispatcherListener;
import com.facebook.react.uimanager.events.RCTEventEmitter;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PowerMockIgnore;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.rule.PowerMockRule;
import org.robolectric.RobolectricTestRunner;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import static com.klarna.inapp.sdk.KlarnaPaymentEvent.EVENT_NAME_ON_AUTHORIZE;
import static com.klarna.inapp.sdk.KlarnaPaymentEvent.EVENT_NAME_ON_ERROR;
import static com.klarna.inapp.sdk.KlarnaPaymentEvent.EVENT_NAME_ON_FINALIZE;
import static com.klarna.inapp.sdk.KlarnaPaymentEvent.EVENT_NAME_ON_INITIALIZE;
import static com.klarna.inapp.sdk.KlarnaPaymentEvent.EVENT_NAME_ON_LOAD;
import static com.klarna.inapp.sdk.KlarnaPaymentEvent.EVENT_NAME_ON_LOAD_PAYMENT_REVIEW;
import static com.klarna.inapp.sdk.KlarnaPaymentEvent.EVENT_NAME_ON_REAUTHORIZE;
import static org.mockito.Mockito.mock;
import static org.powermock.api.mockito.PowerMockito.when;

@PrepareForTest({ReactChoreographer.class, Arguments.class, SystemClock.class})
@RunWith(RobolectricTestRunner.class)
@PowerMockIgnore({"org.mockito.*", "org.robolectric.*", "androidx.*", "android.*", "com.android.*"})
public class KlarnaPaymentEventTest {

    @Rule
    public PowerMockRule rule = new PowerMockRule();

    private CatalystInstance catalystInstance;
    private ReactApplicationContext reactContext;

    private UIManagerModule uiManagerModuleMock;
    private EventDispatcher eventDispatcherMock;

    @Before
    public void setup() {
        reactContext = mock(ReactApplicationContext.class);
        catalystInstance = mock(CatalystInstance.class);

        uiManagerModuleMock = mock(UIManagerModule.class);
        eventDispatcherMock = new EventDispatcher(reactContext);
        when(uiManagerModuleMock.getEventDispatcher())
                .thenReturn(eventDispatcherMock);

        final ReactChoreographer choreographerMock = mock(ReactChoreographer.class);
        PowerMockito.mockStatic(ReactChoreographer.class);
        when(ReactChoreographer.getInstance()).thenAnswer(new Answer<Object>() {
            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable {
                return choreographerMock;
            }
        });

        PowerMockito.mockStatic(Arguments.class);
        PowerMockito.when(Arguments.createArray())
                .thenAnswer(
                        new Answer<Object>() {
                            @Override
                            public Object answer(InvocationOnMock invocation) throws Throwable {
                                return new JavaOnlyArray();
                            }
                        });
        PowerMockito.when(Arguments.createMap())
                .thenAnswer(
                        new Answer<Object>() {
                            @Override
                            public Object answer(InvocationOnMock invocation) throws Throwable {
                                return new JavaOnlyMap();
                            }
                        });
        PowerMockito.mockStatic(SystemClock.class);
    }

    @After
    public void teardown() {
        Mockito.reset(reactContext, catalystInstance, uiManagerModuleMock);
    }

    @Test
    public void testCommandStatics() {
        Assert.assertEquals("onInitialized", EVENT_NAME_ON_INITIALIZE);
        Assert.assertEquals("onLoaded", EVENT_NAME_ON_LOAD);
        Assert.assertEquals("onLoadedPaymentReview", EVENT_NAME_ON_LOAD_PAYMENT_REVIEW);
        Assert.assertEquals("onAuthorized", EVENT_NAME_ON_AUTHORIZE);
        Assert.assertEquals("onReauthorized", EVENT_NAME_ON_REAUTHORIZE);
        Assert.assertEquals("onFinalized", EVENT_NAME_ON_FINALIZE);
        Assert.assertEquals("onError", EVENT_NAME_ON_ERROR);
    }

    @Test
    public void testDispatched() {
        EventDispatcherListener listener = mock(EventDispatcherListener.class);
        eventDispatcherMock.addListener(listener);

        KlarnaPaymentEvent event = new KlarnaPaymentEvent(
                23,
                "testEventName",
                new HashMap<String, Object>() {{
                    put("testKey", "testValue");
                }}
        );

        eventDispatcherMock.dispatchEvent(event);

        Mockito.verify(listener, Mockito.times(1)).onEventDispatch(event);
    }

    @Test
    public void testDispatch() {
        Map<String, Object> additionalParams = Collections.singletonMap("testKey", (Object) "testValue");
        KlarnaPaymentEvent event = new KlarnaPaymentEvent(
                23,
                "testEventName",
                additionalParams
        );

        WritableMap eventData = Arguments.makeNativeMap(additionalParams);

        RCTEventEmitter emitter = mock(RCTEventEmitter.class);
        event.dispatch(emitter);
        Mockito.verify(emitter, Mockito.times(1))
                .receiveEvent(event.getViewTag(), "testEventName", eventData);
    }
}
