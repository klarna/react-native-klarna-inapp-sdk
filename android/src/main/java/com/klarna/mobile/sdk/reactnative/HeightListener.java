package com.klarna.mobile.sdk.reactnative;

import android.os.Build;
import android.webkit.JavascriptInterface;
import android.webkit.ValueCallback;
import android.webkit.WebView;

import java.lang.ref.WeakReference;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Used by the PaymentViewWrapper class to inject JS and listen to height changes inside the WebView
 */
public class HeightListener {
    private final String heightScript = "getAndroidHeight()";
    private final AtomicBoolean attachedEventObserver = new AtomicBoolean(false);
    private WeakReference<HeightListenerCallback> callback;

    HeightListener(WebView webView, HeightListenerCallback callback) {
        this.callback = new WeakReference<>(callback);
        webView.addJavascriptInterface(this, "AndroidEventObserver");
    }

    /**
     * Injects the script that fetches the height value
     *
     * @param webView The web view instance to inject the script into
     */
    void injectListener(WebView webView) {
        injectScript(webView, getHeightFuncScript());
    }

    /**
     * Calls the height function which was injected to the web view and
     * send the value back to the wrapper
     *
     * @param webView The web view to fetch its height value
     */
    void fetchHeight(final WebView webView) {
        if (webView == null) {
            return;
        }
        webView.evaluateJavascript(heightScript, new ValueCallback<String>() {
            @Override
            public void onReceiveValue(String value) {
                if (!attachedEventObserver.get()) {
                    attachEventObserver(webView);
                }
                sendHeightValue(value);
            }
        });
    }

    /**
     * Attaches 'resize' event listener to the web view's window object and
     * calls the height function when there is a resize event, then send the height value
     * back to the wrapper
     *
     * @param webView The web view to attach the 'resize' listener to
     */
    private void attachEventObserver(WebView webView) {
        String eventListenerScript =
                "try{" +
                        "window.addEventListener('resize', function() { AndroidEventObserver.onResized(" + heightScript + "); });" +
                        "}catch(error){" +
                        "console.log('Failed to attach AndroidEventObserver: ' + error);" +
                        "}";
        evaluateJSCompat(webView, eventListenerScript);
    }

    /**
     * Injects the given script into the given web view object
     *
     * @param webView The web view to inject the script into
     * @param script  The script to inject into the web view
     */
    private void injectScript(WebView webView, String script) {
        try {
            evaluateJSCompat(webView, "(function() {" +
                    "var parent = document.getElementsByTagName('head').item(0);" +
                    "var script = document.createElement('script');" +
                    "script.type = 'text/javascript';" +
                    "script.innerHTML = " + script + ";" +
                    "parent.appendChild(script)" +
                    "})()");
        } catch (Throwable t) {
        }
    }

    /**
     * The javascript interface method that is called when there's a
     * 'resize' event
     *
     * @param value The height value which is sent from the 'resize' listener
     */
    @JavascriptInterface
    public void onResized(final String value) {
        attachedEventObserver.set(true);
        sendHeightValue(value);
    }

    /**
     * Unwraps and null checks the callback object and sends
     * the height value to wrapper through this callback
     *
     * @param value The updated height value to send to wrapper
     */
    private void sendHeightValue(String value) {
        if (callback != null) {
            HeightListenerCallback callbackInstance = callback.get();
            if (callbackInstance != null) {
                callbackInstance.onNewHeight(value);
            }
        }
    }

    /**
     * Returns the height function to be injected as a script into the web view
     *
     * @return The height function in JS
     */
    private String getHeightFuncScript() {
        return
                "function getAndroidHeight(){\n" +
                        "    var heights = new Array();\n" +
                        "    var bodyHeight = document.body.scrollHeight;\n" +
                        "    bodyHeight > 0 && heights.push(bodyHeight);\n" +
                        "    var docHeight = document.documentElement.scrollHeight;\n" +
                        "    docHeight > 0 && heights.push(docHeight);\n" +
                        "    try{\n" +
                        "        var containerHeight = document.getElementById('payment-container').scrollHeight;\n" +
                        "        containerHeight > 0 && heights.push(containerHeight);\n" +
                        "    } catch(error){}\n" +
                        "    return heights.length > 0 ? Math.min.apply(Math,heights) : 0;\n" +
                        "}";
    }

    /**
     * Evaluates a script inside a web view in a backward-compatible manner
     *
     * @param webView The web view to evaluate the script
     * @param script  The script to be evaluated
     */
    private void evaluateJSCompat(WebView webView, String script) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            webView.evaluateJavascript(script, null);
        } else {
            webView.loadUrl("javascript:" + script);
        }
    }

    /**
     * The interface between the wrapper and listener class
     * to send the updated height values
     */
    public interface HeightListenerCallback {
        void onNewHeight(String value);
    }
}
