package com.klarna.mobile.sdk.reactnative.standalonewebview;

import android.os.SystemClock;

import com.facebook.react.bridge.Arguments;
import com.facebook.react.bridge.JavaOnlyMap;
import com.facebook.react.bridge.ReactContext;
import com.facebook.react.bridge.ReadableMap;
import com.facebook.react.uimanager.UIManagerHelper;
import com.facebook.react.uimanager.events.EventDispatcher;
import com.klarna.mobile.sdk.api.standalonewebview.KlarnaStandaloneWebView;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.HashMap;
import java.util.Map;

@RunWith(MockitoJUnitRunner.class)
public class KlarnaStandaloneWebViewEventSenderTest {

    private MockedStatic<UIManagerHelper> mockedStaticUiManagerHelper;
    private MockedStatic<SystemClock> mockedStaticSystemClock;
    private MockedStatic<Arguments> mockedStaticArguments;
    private KlarnaStandaloneWebViewEventSender eventSender;
    private KlarnaStandaloneWebView webView;
    private EventDispatcher eventDispatcher;

    @Before
    public void setup() {
        // getUrl() is left unstubbed, so it returns null, as it does when the first load fails.
        webView = Mockito.mock(KlarnaStandaloneWebView.class);
        Mockito.when(webView.getTitle()).thenReturn("Failing page");
        Mockito.when(webView.getProgress()).thenReturn(100);
        Mockito.when(webView.canGoBack()).thenReturn(true);
        Mockito.when(webView.canGoForward()).thenReturn(false);

        eventDispatcher = Mockito.mock(EventDispatcher.class);
        Map<KlarnaStandaloneWebView, ReactContext> viewToReactContext = new HashMap<>();
        viewToReactContext.put(webView, Mockito.mock(ReactContext.class));

        eventSender = new KlarnaStandaloneWebViewEventSender(viewToReactContext);

        mockedStaticUiManagerHelper = Mockito.mockStatic(UIManagerHelper.class);
        Mockito.when(UIManagerHelper.getEventDispatcherForReactTag(Mockito.any(), Mockito.anyInt()))
                .thenAnswer(invocation -> eventDispatcher);
        mockedStaticSystemClock = Mockito.mockStatic(SystemClock.class);
        mockedStaticArguments = Mockito.mockStatic(Arguments.class);
        Mockito.when(Arguments.createMap())
                .thenAnswer(invocation -> new JavaOnlyMap());
    }

    @After
    public void teardown() {
        mockedStaticUiManagerHelper.close();
        mockedStaticSystemClock.close();
        mockedStaticArguments.close();
    }

    // --- sendNavigationErrorEvent ---

    @Test
    public void testSendNavigationErrorEventPutsErrorUnderErrorKey() {
        eventSender.sendNavigationErrorEvent(webView, -10, "net::ERR_UNKNOWN_URL_SCHEME", "quince://test");

        ArgumentCaptor<KlarnaStandaloneWebViewEvent> captor = ArgumentCaptor.forClass(KlarnaStandaloneWebViewEvent.class);
        Mockito.verify(eventDispatcher).dispatchEvent(captor.capture());
        KlarnaStandaloneWebViewEvent event = captor.getValue();
        Assert.assertEquals("onError", event.getEventName());

        // The JS side reads the payload from `nativeEvent.error`, the same key iOS uses.
        ReadableMap error = event.getEventData().getMap("error");
        Assert.assertNotNull(error);
        Assert.assertEquals(-10, error.getInt("code"));
        Assert.assertEquals("net::ERR_UNKNOWN_URL_SCHEME", error.getString("description"));
        Assert.assertEquals("quince://test", error.getString("url"));
        Assert.assertEquals("Failing page", error.getString("title"));
        Assert.assertFalse(error.getBoolean("loading"));
        Assert.assertTrue(error.getBoolean("canGoBack"));
        Assert.assertFalse(error.getBoolean("canGoForward"));
    }
}
