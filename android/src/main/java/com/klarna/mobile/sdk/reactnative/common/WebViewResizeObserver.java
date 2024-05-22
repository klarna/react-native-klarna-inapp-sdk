package com.klarna.mobile.sdk.reactnative.common;

import android.os.Build;
import android.webkit.JavascriptInterface;
import android.webkit.WebView;

import java.lang.ref.WeakReference;

public class WebViewResizeObserver {
    /**
     * The interface between the wrapper and listener class
     * to send the updated height values
     */
    public interface WebViewResizeObserverCallback {
        void onResized(int value);
    }

    public enum TargetElement {
        PAYMENT_CONTAINER,
        CHECKOUT_CONTAINER
    }

    private static final String JS_INTERFACE_NAME = "NativeResizeObserver";
    private final WeakReference<WebViewResizeObserverCallback> callback;
    private final TargetElement targetElement;

    public WebViewResizeObserver(WebViewResizeObserverCallback callback, TargetElement targetElement) {
        this.callback = new WeakReference<>(callback);
        this.targetElement = targetElement;
    }

    public void addInterface(WebView webView) {
        webView.addJavascriptInterface(this, JS_INTERFACE_NAME);
    }

    public void injectListener(WebView webView) {
        injectScript(webView, initScript());
    }

    /**
     * The javascript interface method that is called when there's a
     * 'resize' event
     *
     * @param value The height value which is sent from the 'resize' listener
     */
    @JavascriptInterface
    public void onResized(int value) {
        sendHeightValue(value);
    }

    /**
     * Unwraps and null checks the callback object and sends
     * the height value to wrapper through this callback
     *
     * @param value The updated height value to send to wrapper
     */
    private void sendHeightValue(int value) {
        WebViewResizeObserverCallback callbackInstance = callback.get();
        if (callbackInstance != null) {
            callbackInstance.onResized(value);
        }
    }

    private void injectScript(WebView webView, String script) {
        String tryCatchScript = "try {\n" +
                "    " + script + "\n" +
                "} catch (error) {\n" +
                "    console.error(\"inject failed\")\n" +
                "}";
        evaluateJSCompat(webView, tryCatchScript);
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

    private String initScript() {
        StringBuilder scriptBuilder = new StringBuilder();
        switch (targetElement) {
            case PAYMENT_CONTAINER:
                scriptBuilder.append("console.log('Resize observer injection started');\n");
                scriptBuilder.append("const container = document.querySelector('#payment-container');\n");
                scriptBuilder.append("console.log('Resize observer container selected: ', container);\n");
                scriptBuilder.append("const resizeObserver = new ResizeObserver((entries) => {\n" +
                        "    console.log('Container size changed.', entries);\n" +
                        "    for (let entry of entries) {\n" +
                        "        console.log('New dimensions found: ', entry);\n" +
                        "        if (entry.contentRect) {\n" +
                        "            const height = entry.contentRect.height;\n" +
                        "            const listener = window.NativeResizeObserver;\n" +
                        "            if (listener != null) {\n" +
                        "                listener.onResized(height);\n" +
                        "                console.log('Container size sent to native: ', height);\n" +
                        "            }\n" +
                        "        }\n" +
                        "    }\n" +
                        "});\n");
                scriptBuilder.append("console.log('Resize observer initialized.');\n");
                scriptBuilder.append("if (container != null) {\n" +
                        "    resizeObserver.observe(container);\n" +
                        "    console.log('Resize observer set to component.');\n" +
                        "}\n");
                scriptBuilder.append("console.log('Resize observer injection finished.');");
                break;
            case CHECKOUT_CONTAINER:
                scriptBuilder.append("const kcoIframe = document.getElementById('klarna-checkout-iframe')\n");
                scriptBuilder.append("window.addEventListener('message', (message) => {\n" +
                        "                if (message.source !== kcoIframe && message.data) {\n" +
                        "                    try {\n" +
                        "                        const data = JSON.parse(message.data)\n" +
                        "                        if (data.event === 'frame:checkout:resize') {\n" +
                        "                            const listener = window.NativeResizeObserver;\n" +
                        "                            if (listener != null) {\n" +
                        "                                const height = data.args[0];" +
                        "                                listener.onResized(height);\n" +
                        "                                console.log('Container size sent to native: ', height);\n" +
                        "                            } else {\n" +
                        "                                console.log('listener is null!')\n" +
                        "                            }\n" +
                        "                        }\n" +
                        "                    } catch (error) {\n" +
                        "                        console.log('Could not parse message.data', message);\n" +
                        "                    }\n" +
                        "                 }\n" +
                        "             });\n");
                break;
        }
        return scriptBuilder.toString();
    }
}
