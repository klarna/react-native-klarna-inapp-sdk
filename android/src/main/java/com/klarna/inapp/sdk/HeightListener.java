package com.klarna.inapp.sdk;

import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.webkit.JavascriptInterface;
import android.webkit.ValueCallback;
import android.webkit.WebView;

import java.lang.ref.WeakReference;
import java.util.concurrent.atomic.AtomicBoolean;

public class HeightListener {
    private final String heightScript = "getAndroidHeight()";
    private final AtomicBoolean attachedEventObserver = new AtomicBoolean(false);
    private WeakReference<HeightListenerCallback> callback;

    HeightListener(WebView webView, HeightListenerCallback callback){
        this.callback = new WeakReference<>(callback);
        webView.addJavascriptInterface(this, "AndroidEventObserver");
    }

    void injectListener(WebView webView){
        injectScript(webView, getHeightFuncScript());
    }

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

    private void attachEventObserver(WebView webView) {
        String eventListenerScript =
                "try{" +
                        "window.addEventListener('resize', function() { AndroidEventObserver.onResized(" + heightScript + "); });" +
                        "}catch(error){" +
                        "console.log('Failed to attach AndroidEventObserver: ' + error);" +
                        "}";
        evaluateJSCompat(webView, eventListenerScript);
    }

    private void injectScript(WebView webView, String script) {
        try {
            evaluateJSCompat(webView, "(function() {" +
                    "var parent = document.getElementsByTagName('head').item(0);" +
                    "var script = document.createElement('script');" +
                    "script.type = 'text/javascript';" +
                    "script.innerHTML = " + script + ";" +
                    "parent.appendChild(script)" +
                    "})()");
        } catch (Throwable t) { }
    }

    @JavascriptInterface
    public void onResized(final String value) {
        attachedEventObserver.set(true);
        sendHeightValue(value);
    }

    private void sendHeightValue(String value){
        if(callback != null){
            HeightListenerCallback callbackInstance = callback.get();
            if(callbackInstance != null){
                callbackInstance.onNewHeight(value);
            }
        }
    }

    private String getHeightFuncScript(){
        return
                "function getAndroidHeight(){\n" +
                        "    var heights = new Array;\n" +
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

    private void evaluateJSCompat(WebView webView, String script){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            webView.evaluateJavascript(script, null);
        } else {
            webView.loadUrl("javascript:" + script);
        }
    }

    public interface HeightListenerCallback{
        void onNewHeight(String value);
    }
}
