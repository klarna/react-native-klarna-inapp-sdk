package com.klarna.mobile.sdk.reactnative.standalonewebview;

import android.webkit.CookieManager;
import android.webkit.ValueCallback;

import com.facebook.react.bridge.JavaOnlyArray;
import com.klarna.mobile.sdk.api.standalonewebview.KlarnaStandaloneWebView;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InOrder;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import java.util.Collections;
import java.util.List;
import java.util.Map;

@RunWith(RobolectricTestRunner.class)
@Config(sdk = 33)
public class KlarnaStandaloneWebViewManagerCookiesTest {

    private static final String URL = "https://www.example.com/checkout";
    private static final String COOKIES_JSON = "["
            + "{\"name\":\"session\",\"value\":\"abc\",\"domain\":\"www.example.com\",\"path\":\"/\"},"
            + "{\"name\":\"store\",\"value\":\"42\",\"domain\":\"example.com\",\"path\":\"/\"}]";

    private MockedStatic<CookieManager> mockedStaticCookieManager;
    private CookieManager cookieManager;
    private KlarnaStandaloneWebViewEventSender eventSender;
    private KlarnaStandaloneWebViewManager manager;
    private KlarnaStandaloneWebView view;

    @Before
    public void setup() {
        eventSender = Mockito.mock(KlarnaStandaloneWebViewEventSender.class);
        manager = new KlarnaStandaloneWebViewManager(eventSender);
        view = Mockito.mock(KlarnaStandaloneWebView.class);
        cookieManager = Mockito.mock(CookieManager.class);
        mockedStaticCookieManager = Mockito.mockStatic(CookieManager.class);
        mockedStaticCookieManager.when(CookieManager::getInstance).thenReturn(cookieManager);
    }

    @After
    public void teardown() {
        mockedStaticCookieManager.close();
    }

    @SuppressWarnings("unchecked")
    private List<ValueCallback<Boolean>> captureCookieCallbacks(int count) {
        ArgumentCaptor<ValueCallback<Boolean>> callbacks = ArgumentCaptor.forClass(ValueCallback.class);
        Mockito.verify(cookieManager, Mockito.times(count))
                .setCookie(Mockito.anyString(), Mockito.anyString(), callbacks.capture());
        return callbacks.getAllValues();
    }

    // --- load ---

    @Test
    public void testLoadCommandFromOlderJsWithOnlyTheUrlLoadsUrl() {
        manager.receiveCommand(view, KlarnaStandaloneWebViewManager.COMMAND_LOAD, JavaOnlyArray.of(URL));

        Mockito.verify(view).loadUrl(URL);
    }

    @Test
    public void testLoadCommandWithEmptyOptionsLoadsUrl() {
        manager.receiveCommand(view, KlarnaStandaloneWebViewManager.COMMAND_LOAD, JavaOnlyArray.of(URL, ""));

        Mockito.verify(view).loadUrl(URL);
    }

    @Test
    public void testLoadCommandWithHeadersSendsThemWithTheUrl() {
        manager.receiveCommand(view, KlarnaStandaloneWebViewManager.COMMAND_LOAD,
                JavaOnlyArray.of(URL, "{\"headers\":{\"X-Store\":\"42\"}}"));

        Map<String, String> expected = Collections.singletonMap("X-Store", "42");
        Mockito.verify(view).loadUrl(URL, expected);
        Mockito.verify(view, Mockito.never()).loadUrl(URL);
    }

    @Test
    public void testLoadCommandWithInvalidOptionsLoadsUrlWithoutHeaders() {
        manager.receiveCommand(view, KlarnaStandaloneWebViewManager.COMMAND_LOAD, JavaOnlyArray.of(URL, "{not json"));

        Mockito.verify(view).loadUrl(URL);
    }

    @Test
    public void testLoadDoesNotTouchCookies() {
        manager.receiveCommand(view, KlarnaStandaloneWebViewManager.COMMAND_LOAD,
                JavaOnlyArray.of(URL, "{\"headers\":{\"X-Store\":\"42\"}}"));

        Mockito.verifyNoInteractions(cookieManager);
    }

    // --- setCookies ---

    @Test
    public void testSetCookiesCommandStoresEachCookieForItsDomain() {
        manager.receiveCommand(view, KlarnaStandaloneWebViewManager.COMMAND_SET_COOKIES, JavaOnlyArray.of(7, COOKIES_JSON));

        Mockito.verify(cookieManager).setCookie(
                Mockito.eq("https://www.example.com/"), Mockito.eq("session=abc; Domain=www.example.com; Path=/"), Mockito.any());
        Mockito.verify(cookieManager).setCookie(
                Mockito.eq("https://example.com/"), Mockito.eq("store=42; Domain=example.com; Path=/"), Mockito.any());
    }

    @Test
    public void testSetCookiesReportsSuccessOnlyOnceEveryCookieIsStored() {
        manager.setCookies(view, 7, COOKIES_JSON);
        List<ValueCallback<Boolean>> callbacks = captureCookieCallbacks(2);

        callbacks.get(0).onReceiveValue(true);
        Mockito.verifyNoInteractions(eventSender);

        callbacks.get(1).onReceiveValue(true);
        InOrder inOrder = Mockito.inOrder(cookieManager, eventSender);
        inOrder.verify(cookieManager).flush();
        inOrder.verify(eventSender).sendSetCookiesResultEvent(view, 7, "");
    }

    @Test
    public void testSetCookiesReportsTheCookiesThatWereNotStored() {
        manager.setCookies(view, 7, COOKIES_JSON);
        List<ValueCallback<Boolean>> callbacks = captureCookieCallbacks(2);

        callbacks.get(0).onReceiveValue(false);
        callbacks.get(1).onReceiveValue(true);

        Mockito.verify(eventSender).sendSetCookiesResultEvent(view, 7, "Could not store the cookies session.");
    }

    private static final String REPLACING_JSON = "[{\"name\":\"session\",\"value\":\"new\",\"domain\":\"www.example.com\","
            + "\"path\":\"/\",\"hostOnly\":true,\"replaceConflicting\":true,\"requestPath\":\"/checkout/payment\"}]";

    @Test
    @SuppressWarnings("unchecked")
    public void testReplacingCookieExpiresSameNameCookiesBeforeStoringTheNewOne() {
        manager.setCookies(view, 7, REPLACING_JSON);

        ArgumentCaptor<String> values = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<ValueCallback<Boolean>> callbacks = ArgumentCaptor.forClass(ValueCallback.class);
        // 4 paths (/, /checkout, /checkout/, /checkout/payment) x (host-only, www.example.com, example.com)
        Mockito.verify(cookieManager, Mockito.times(12))
                .setCookie(Mockito.anyString(), values.capture(), callbacks.capture());
        Mockito.verify(cookieManager, Mockito.never()).setCookie(Mockito.anyString(), Mockito.contains("=new"), Mockito.any());
        org.junit.Assert.assertTrue(values.getAllValues().contains(
                "session=; Path=/checkout; Max-Age=0; Expires=Thu, 01 Jan 1970 00:00:00 GMT; Secure"));
        org.junit.Assert.assertTrue(values.getAllValues().contains(
                "session=; Path=/checkout/payment; Max-Age=0; Expires=Thu, 01 Jan 1970 00:00:00 GMT; Secure"));
        org.junit.Assert.assertTrue(values.getAllValues().contains(
                "session=; Domain=example.com; Path=/; Max-Age=0; Expires=Thu, 01 Jan 1970 00:00:00 GMT; Secure"));

        for (ValueCallback<Boolean> callback : callbacks.getAllValues()) {
            callback.onReceiveValue(false);
        }

        Mockito.verify(cookieManager).setCookie(
                Mockito.eq("https://www.example.com/"), Mockito.eq("session=new; Path=/"), Mockito.any());
    }

    @Test
    public void testCookiesWithoutReplaceConflictingExpireNothing() {
        manager.setCookies(view, 7, COOKIES_JSON);

        Mockito.verify(cookieManager, Mockito.times(2)).setCookie(Mockito.anyString(), Mockito.anyString(), Mockito.any());
        Mockito.verify(cookieManager, Mockito.never()).setCookie(Mockito.anyString(), Mockito.contains("Max-Age=0"), Mockito.any());
    }

    @Test
    public void testSetCookiesWithInvalidJsonReportsAnError() {
        manager.setCookies(view, 7, "{not json");

        Mockito.verifyNoInteractions(cookieManager);
        Mockito.verify(eventSender).sendSetCookiesResultEvent(
                Mockito.eq(view), Mockito.eq(7), Mockito.startsWith("Invalid cookies"));
    }

    @Test
    public void testSetCookiesWithNoCookiesReportsSuccess() {
        manager.setCookies(view, 7, "[]");

        Mockito.verifyNoInteractions(cookieManager);
        Mockito.verify(eventSender).sendSetCookiesResultEvent(view, 7, "");
    }

    @Test
    public void testSetCookiesCommandWithoutArgumentsIsIgnored() {
        manager.receiveCommand(view, KlarnaStandaloneWebViewManager.COMMAND_SET_COOKIES, JavaOnlyArray.of(7));

        Mockito.verifyNoInteractions(cookieManager, eventSender);
    }
}
