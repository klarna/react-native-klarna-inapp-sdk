package com.klarna.mobile.sdk.reactnative.common;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import android.webkit.ValueCallback;
import android.webkit.WebView;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import org.mozilla.javascript.Context;
import org.mozilla.javascript.EvaluatorException;
import org.robolectric.RobolectricTestRunner;

/**
 * The injected script is a Java string, so a syntax error in it reaches neither the Java
 * compiler nor the script's own try/catch — the web view discards it whole at parse time.
 */
@RunWith(RobolectricTestRunner.class)
public class WebViewResizeObserverTest {

    /** Captures the payload as handed to the web view, so nothing can drift from the source. */
    private String injectedScriptFor(WebViewResizeObserver.TargetElement targetElement) {
        WebViewResizeObserver observer = new WebViewResizeObserver(value -> { }, targetElement);
        WebView webView = Mockito.mock(WebView.class);

        observer.injectListener(webView);

        ArgumentCaptor<String> script = ArgumentCaptor.forClass(String.class);
        Mockito.verify(webView).evaluateJavascript(script.capture(), Mockito.<ValueCallback<String>>isNull());
        return script.getValue();
    }

    /** Compiles without executing, so early errors surface with their identifier and line. */
    private void assertParses(String script) {
        Context context = Context.enter();
        try {
            context.setLanguageVersion(Context.VERSION_ES6);
            context.compileString(script, "injected.js", 1, null);
        } catch (EvaluatorException e) {
            fail("Injected script does not parse: " + e.details() + " (line " + e.lineNumber() + ")\n\n" + script);
        } finally {
            Context.exit();
        }
    }

    // region parses

    @Test
    public void testInjectListener_paymentContainerScriptParses() {
        // Given
        WebViewResizeObserver.TargetElement targetElement = WebViewResizeObserver.TargetElement.PAYMENT_CONTAINER;

        // When
        String script = injectedScriptFor(targetElement);

        // Then
        assertParses(script);
    }

    @Test
    public void testInjectListener_checkoutContainerScriptParses() {
        // Given
        WebViewResizeObserver.TargetElement targetElement = WebViewResizeObserver.TargetElement.CHECKOUT_CONTAINER;

        // When
        String script = injectedScriptFor(targetElement);

        // Then
        assertParses(script);
    }

    // endregion

    // region re-injection

    /**
     * Re-injecting does not redeclare, since the script's declarations are scoped to the try
     * block wrapping them. Without a reachable handle it would stack observers on one element.
     */
    @Test
    public void testInjectListener_paymentContainerDisconnectsPreviousObserver() {
        // Given
        String handle = "window." + WebViewResizeObserver.JS_PAYMENT_OBSERVER_HANDLE;

        // When
        String script = injectedScriptFor(WebViewResizeObserver.TargetElement.PAYMENT_CONTAINER);

        // Then
        assertTrue("a previously stored observer must be disconnected", script.contains(handle + ".disconnect()"));
        assertTrue("the new observer must be stored for the next injection", script.contains(handle + " = "));
    }

    // endregion
}
