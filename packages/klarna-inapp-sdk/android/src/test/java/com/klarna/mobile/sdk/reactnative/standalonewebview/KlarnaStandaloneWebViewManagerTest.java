package com.klarna.mobile.sdk.reactnative.standalonewebview;

import android.app.Activity;
import android.net.Uri;
import android.os.Build;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;

import com.facebook.react.bridge.Arguments;
import com.facebook.react.bridge.JavaOnlyMap;
import com.facebook.react.uimanager.ThemedReactContext;
import com.facebook.react.uimanager.UIManagerHelper;
import com.facebook.react.uimanager.events.EventDispatcher;
import com.klarna.mobile.sdk.api.standalonewebview.KlarnaStandaloneWebView;
import com.klarna.mobile.sdk.api.standalonewebview.KlarnaStandaloneWebViewClient;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.MockedConstruction;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import java.util.ArrayList;
import java.util.List;

@RunWith(RobolectricTestRunner.class)
public class KlarnaStandaloneWebViewManagerTest {

    private static final int VIEW_TAG = 7;
    private static final int ERROR_CODE = -10;
    private static final String ERROR_DESCRIPTION = "net::ERR_UNKNOWN_URL_SCHEME";
    private static final String FAILING_URL = "quince://test";

    private MockedStatic<Arguments> mockedStaticArguments;
    private MockedStatic<UIManagerHelper> mockedStaticUiManagerHelper;
    private MockedConstruction<KlarnaStandaloneWebView> mockedWebViews;
    private final List<List<?>> webViewConstructorArguments = new ArrayList<>();

    private KlarnaStandaloneWebViewManager manager;
    private ThemedReactContext viewContext;
    private Activity viewActivity;
    private EventDispatcher viewEventDispatcher;
    private EventDispatcher otherEventDispatcher;

    private KlarnaStandaloneWebViewEventSender eventSender;
    private KlarnaStandaloneWebViewClient webViewClient;
    private KlarnaStandaloneWebView failingWebView;

    @Before
    public void setup() {
        // On the old architecture, RN keeps view managers across JS reloads, so each view must use
        // the React context it was created with.
        manager = new KlarnaStandaloneWebViewManager();

        viewActivity = Mockito.mock(Activity.class);
        viewContext = Mockito.mock(ThemedReactContext.class);
        Mockito.when(viewContext.getCurrentActivity()).thenReturn(viewActivity);

        viewEventDispatcher = Mockito.mock(EventDispatcher.class);
        otherEventDispatcher = Mockito.mock(EventDispatcher.class);
        mockedStaticUiManagerHelper = Mockito.mockStatic(UIManagerHelper.class);
        mockedStaticUiManagerHelper.when(() -> UIManagerHelper.getEventDispatcherForReactTag(Mockito.any(), Mockito.anyInt()))
                .thenReturn(otherEventDispatcher);
        mockedStaticUiManagerHelper.when(() -> UIManagerHelper.getEventDispatcherForReactTag(Mockito.eq(viewContext), Mockito.anyInt()))
                .thenReturn(viewEventDispatcher);

        mockedStaticArguments = Mockito.mockStatic(Arguments.class);
        mockedStaticArguments.when(Arguments::createMap).thenAnswer(invocation -> new JavaOnlyMap());

        // A real KlarnaStandaloneWebView starts the native SDK, so the mocks only record what they were created with.
        mockedWebViews = Mockito.mockConstruction(KlarnaStandaloneWebView.class, (webView, context) -> {
            webViewConstructorArguments.add(context.arguments());
            Mockito.when(webView.getId()).thenReturn(VIEW_TAG);
        });

        // The onReceivedError tests check the sender calls directly, so they use a manager with a mocked sender.
        try (MockedConstruction<KlarnaStandaloneWebViewEventSender> mockedEventSenders = Mockito.mockConstruction(KlarnaStandaloneWebViewEventSender.class)) {
            webViewClient = new KlarnaStandaloneWebViewManager().klarnaStandaloneWebViewClient;
            eventSender = mockedEventSenders.constructed().get(0);
        }
        failingWebView = Mockito.mock(KlarnaStandaloneWebView.class);
    }

    @After
    public void teardown() {
        mockedWebViews.close();
        mockedStaticArguments.close();
        mockedStaticUiManagerHelper.close();
    }

    @Test
    public void testViewIsCreatedWithTheActivityOfItsOwnReactContext() {
        manager.createViewInstance(viewContext);

        Assert.assertEquals(1, webViewConstructorArguments.size());
        Assert.assertSame(viewActivity, webViewConstructorArguments.get(0).get(0));
    }

    @Test
    public void testEventsAreSentThroughTheViewsOwnReactContext() {
        KlarnaStandaloneWebView webView = manager.createViewInstance(viewContext);

        // The SDK reports page loads to the client it was created with.
        webViewClientGivenToTheSdk().onPageStarted(webView, "https://example.com", null);

        ArgumentCaptor<KlarnaStandaloneWebViewEvent> captor = ArgumentCaptor.forClass(KlarnaStandaloneWebViewEvent.class);
        Mockito.verify(viewEventDispatcher).dispatchEvent(captor.capture());
        Assert.assertEquals("onLoadStart", captor.getValue().getEventName());
        Assert.assertEquals(VIEW_TAG, captor.getValue().getViewTag());
        Mockito.verifyNoInteractions(otherEventDispatcher);
    }

    @Test
    public void testEachViewKeepsTheReactContextItWasCreatedWith() {
        // After a JS reload the same manager creates new views with a new context.
        ThemedReactContext reloadedContext = Mockito.mock(ThemedReactContext.class);
        EventDispatcher reloadedEventDispatcher = Mockito.mock(EventDispatcher.class);
        mockedStaticUiManagerHelper.when(() -> UIManagerHelper.getEventDispatcherForReactTag(Mockito.eq(reloadedContext), Mockito.anyInt()))
                .thenReturn(reloadedEventDispatcher);

        KlarnaStandaloneWebView oldWebView = manager.createViewInstance(viewContext);
        KlarnaStandaloneWebView newWebView = manager.createViewInstance(reloadedContext);
        webViewClientGivenToTheSdk().onPageStarted(oldWebView, "https://example.com", null);
        webViewClientGivenToTheSdk().onPageStarted(newWebView, "https://example.com", null);

        Mockito.verify(viewEventDispatcher).dispatchEvent(Mockito.any(KlarnaStandaloneWebViewEvent.class));
        Mockito.verify(reloadedEventDispatcher).dispatchEvent(Mockito.any(KlarnaStandaloneWebViewEvent.class));
        Mockito.verifyNoInteractions(otherEventDispatcher);
    }

    @Test
    public void testEventsAreNotSentAfterTheViewIsDropped() {
        KlarnaStandaloneWebView webView = manager.createViewInstance(viewContext);

        manager.onDropViewInstance(webView);
        webViewClientGivenToTheSdk().onPageStarted(webView, "https://example.com", null);

        Mockito.verifyNoInteractions(viewEventDispatcher, otherEventDispatcher);
    }

    // --- onReceivedError ---

    @Test
    @Config(sdk = Build.VERSION_CODES.M)
    public void testMainFrameErrorIsSentOnceFromApi23() {
        WebResourceRequest request = Mockito.mock(WebResourceRequest.class);
        Mockito.when(request.isForMainFrame()).thenReturn(true);
        Mockito.when(request.getUrl()).thenReturn(Uri.parse(FAILING_URL));
        WebResourceError error = Mockito.mock(WebResourceError.class);
        Mockito.when(error.getErrorCode()).thenReturn(ERROR_CODE);
        Mockito.when(error.getDescription()).thenReturn(ERROR_DESCRIPTION);

        // From API 23, WebView reports a main-frame error to both callbacks.
        webViewClient.onReceivedError(failingWebView, request, error);
        webViewClient.onReceivedError(failingWebView, ERROR_CODE, ERROR_DESCRIPTION, FAILING_URL);

        Mockito.verify(eventSender).sendNavigationErrorEvent(failingWebView, ERROR_CODE, ERROR_DESCRIPTION, FAILING_URL);
        Mockito.verifyNoMoreInteractions(eventSender);
    }

    @Test
    @Config(sdk = Build.VERSION_CODES.M)
    public void testMainFrameErrorWithoutRequestUrlIsSentWithoutUrl() {
        // getUrl() is not annotated @NonNull, so a missing URL must not crash the callback.
        WebResourceRequest request = Mockito.mock(WebResourceRequest.class);
        Mockito.when(request.isForMainFrame()).thenReturn(true);
        WebResourceError error = Mockito.mock(WebResourceError.class);
        Mockito.when(error.getErrorCode()).thenReturn(ERROR_CODE);
        Mockito.when(error.getDescription()).thenReturn(ERROR_DESCRIPTION);

        webViewClient.onReceivedError(failingWebView, request, error);

        Mockito.verify(eventSender).sendNavigationErrorEvent(failingWebView, ERROR_CODE, ERROR_DESCRIPTION, null);
        Mockito.verifyNoMoreInteractions(eventSender);
    }

    @Test
    @Config(sdk = Build.VERSION_CODES.LOLLIPOP_MR1)
    public void testErrorIsSentBeforeApi23() {
        // Before API 23, WebView only has the deprecated callback.
        webViewClient.onReceivedError(failingWebView, ERROR_CODE, ERROR_DESCRIPTION, FAILING_URL);

        Mockito.verify(eventSender).sendNavigationErrorEvent(failingWebView, ERROR_CODE, ERROR_DESCRIPTION, FAILING_URL);
        Mockito.verifyNoMoreInteractions(eventSender);
    }

    private KlarnaStandaloneWebViewClient webViewClientGivenToTheSdk() {
        for (Object argument : webViewConstructorArguments.get(0)) {
            if (argument instanceof KlarnaStandaloneWebViewClient) {
                return (KlarnaStandaloneWebViewClient) argument;
            }
        }
        throw new AssertionError("No KlarnaStandaloneWebViewClient was passed to KlarnaStandaloneWebView");
    }
}
