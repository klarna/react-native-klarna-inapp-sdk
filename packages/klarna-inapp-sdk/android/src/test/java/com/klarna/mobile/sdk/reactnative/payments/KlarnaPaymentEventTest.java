package com.klarna.mobile.sdk.reactnative.payments;

import static com.klarna.mobile.sdk.reactnative.payments.KlarnaPaymentEvent.EVENT_NAME_ON_AUTHORIZE;
import static com.klarna.mobile.sdk.reactnative.payments.KlarnaPaymentEvent.EVENT_NAME_ON_ERROR;
import static com.klarna.mobile.sdk.reactnative.payments.KlarnaPaymentEvent.EVENT_NAME_ON_FINALIZE;
import static com.klarna.mobile.sdk.reactnative.payments.KlarnaPaymentEvent.EVENT_NAME_ON_INITIALIZE;
import static com.klarna.mobile.sdk.reactnative.payments.KlarnaPaymentEvent.EVENT_NAME_ON_LOAD;
import static com.klarna.mobile.sdk.reactnative.payments.KlarnaPaymentEvent.EVENT_NAME_ON_LOAD_PAYMENT_REVIEW;
import static com.klarna.mobile.sdk.reactnative.payments.KlarnaPaymentEvent.EVENT_NAME_ON_REAUTHORIZE;
import static org.mockito.Mockito.mock;

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
import com.facebook.react.uimanager.events.EventDispatcherImpl;
import com.facebook.react.uimanager.events.EventDispatcherListener;
import com.facebook.react.uimanager.events.RCTEventEmitter;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.junit.MockitoJUnitRunner;
import org.mockito.stubbing.Answer;

@RunWith(MockitoJUnitRunner.class)
public class KlarnaPaymentEventTest {

    private MockedStatic<ReactChoreographer> mockedStaticReactChoreographer;
    private MockedStatic<Arguments> mockedStaticArguments;
    private MockedStatic<SystemClock> mockedStaticSystemClock;

    private CatalystInstance catalystInstance;
    private ReactApplicationContext reactContext;
    private EventDispatcher eventDispatcherMock;

    @Before
    public void setup() {
        reactContext = mock(ReactApplicationContext.class);
        catalystInstance = mock(CatalystInstance.class);

        eventDispatcherMock = new EventDispatcherImpl(reactContext);

        final ReactChoreographer choreographerMock = mock(ReactChoreographer.class);
        mockedStaticReactChoreographer = Mockito.mockStatic(ReactChoreographer.class);
        Mockito.when(ReactChoreographer.getInstance()).thenAnswer(new Answer<Object>() {
            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable {
                return choreographerMock;
            }
        });

        mockedStaticArguments = Mockito.mockStatic(Arguments.class);
        Mockito.when(Arguments.createArray())
                .thenAnswer(
                        new Answer<Object>() {
                            @Override
                            public Object answer(InvocationOnMock invocation) throws Throwable {
                                return new JavaOnlyArray();
                            }
                        });
        Mockito.when(Arguments.createMap())
                .thenAnswer(
                        new Answer<Object>() {
                            @Override
                            public Object answer(InvocationOnMock invocation) throws Throwable {
                                return new JavaOnlyMap();
                            }
                        });
        mockedStaticSystemClock = Mockito.mockStatic(SystemClock.class);
    }

    @After
    public void teardown() {
        Mockito.reset(reactContext, catalystInstance);
        mockedStaticReactChoreographer.close();
        mockedStaticArguments.close();
        mockedStaticSystemClock.close();
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

        WritableMap map = Arguments.createMap();
        map.putString("testKey", "testValue");

        KlarnaPaymentEvent event = new KlarnaPaymentEvent(
                23,
                "testEventName",
                map
        );

        eventDispatcherMock.dispatchEvent(event);

        Mockito.verify(listener, Mockito.times(1)).onEventDispatch(event);
    }

    @Test
    public void testDispatch() {
        WritableMap map = Arguments.createMap();
        map.putString("testKey", "testValue");

        KlarnaPaymentEvent event = new KlarnaPaymentEvent(
                23,
                "testEventName",
                map
        );

        RCTEventEmitter emitter = mock(RCTEventEmitter.class);
        event.dispatch(emitter);
        Mockito.verify(emitter, Mockito.times(1))
                .receiveEvent(event.getViewTag(), "testEventName", map);
    }
}
