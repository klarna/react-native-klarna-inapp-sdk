package com.klarna.mobile.sdk.reactnative.standalonewebview;

import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.text.TextUtils;
import android.view.View;
import android.webkit.CookieManager;
import android.webkit.RenderProcessGoneDetail;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.VisibleForTesting;

import com.facebook.react.bridge.ReactContext;
import com.facebook.react.bridge.ReadableArray;
import com.facebook.react.common.MapBuilder;
import com.facebook.react.uimanager.ThemedReactContext;
import com.facebook.react.uimanager.annotations.ReactProp;
import com.klarna.mobile.sdk.KlarnaMobileSDKError;
import com.klarna.mobile.sdk.api.KlarnaEventHandler;
import com.klarna.mobile.sdk.api.KlarnaProductEvent;
import com.klarna.mobile.sdk.api.KlarnaTheme;
import com.klarna.mobile.sdk.api.component.KlarnaComponent;
import com.klarna.mobile.sdk.api.standalonewebview.KlarnaStandaloneWebView;
import com.klarna.mobile.sdk.api.standalonewebview.KlarnaStandaloneWebViewClient;
import com.klarna.mobile.sdk.reactnative.spec.RNKlarnaStandaloneWebViewSpec;

import org.json.JSONException;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.WeakHashMap;

public class KlarnaStandaloneWebViewManager extends RNKlarnaStandaloneWebViewSpec<KlarnaStandaloneWebView> {

    private static final String REACT_CLASS = "RNKlarnaStandaloneWebView";

    // Commands that can be triggered from React Native side
    public static final String COMMAND_LOAD = "load";
    public static final String COMMAND_GO_FORWARD = "goForward";
    public static final String COMMAND_GO_BACK = "goBack";
    public static final String COMMAND_RELOAD = "reload";
    public static final String COMMAND_STOP_LOADING = "stopLoading";
    public static final String COMMAND_SET_COOKIES = "setCookies";

    private static final String OVER_SCROLL_MODE_ALWAYS = "always";
    private static final String OVER_SCROLL_MODE_CONTENT = "content";
    private static final String OVER_SCROLL_MODE_NEVER = "never";

    // The React context of each view, used to send its events. On the old architecture RN reuses this
    // manager after a JS reload, so events can't go through a context stored when the manager was created.
    // The context holds the host Activity, so onDropViewInstance removes the entry rather than leaving it to GC.
    private final Map<KlarnaStandaloneWebView, ReactContext> viewToReactContext = new WeakHashMap<>();
    private final KlarnaStandaloneWebViewEventSender klarnaStandaloneWebViewEventSender;
    private final KlarnaEventHandler klarnaEventHandler = new KlarnaEventHandler() {
        @Override
        public void onEvent(@NonNull KlarnaComponent klarnaComponent, @NonNull KlarnaProductEvent klarnaProductEvent) {
            if (klarnaComponent instanceof KlarnaStandaloneWebView) {
                klarnaStandaloneWebViewEventSender.sendKlarnaMessageEvent((KlarnaStandaloneWebView) klarnaComponent, klarnaProductEvent);
            }
        }

        @Override
        public void onError(@NonNull KlarnaComponent klarnaComponent, @NonNull KlarnaMobileSDKError klarnaMobileSDKError) {
            // Not used as of now
        }
    };
    @VisibleForTesting
    final KlarnaStandaloneWebViewClient klarnaStandaloneWebViewClient = new KlarnaStandaloneWebViewClient() {
        @Override
        public void onPageStarted(@Nullable KlarnaStandaloneWebView view, @Nullable String url, @Nullable Bitmap favicon) {
            klarnaStandaloneWebViewEventSender.sendNavigationEvent(view, KlarnaStandaloneWebViewEvent.Event.ON_LOAD_START, url);
        }

        @Override
        public void onPageFinished(@Nullable KlarnaStandaloneWebView view, @Nullable String url) {
            klarnaStandaloneWebViewEventSender.sendNavigationEvent(view, KlarnaStandaloneWebViewEvent.Event.ON_LOAD_END, url);
        }

        @Override
        public void onReceivedError(@Nullable KlarnaStandaloneWebView view, @Nullable WebResourceRequest request, @Nullable WebResourceError error) {
            // Do not send a navigation error event if the request is not for the main frame.
            if (request != null && !request.isForMainFrame()) {
                return;
            }
            if (error != null && Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                Uri requestUrl = request != null ? request.getUrl() : null;
                String failingUrl = requestUrl != null ? requestUrl.toString() : null;
                klarnaStandaloneWebViewEventSender.sendNavigationErrorEvent(view, error.getErrorCode(), error.getDescription().toString(), failingUrl);
            }
        }

        @Override
        public void onReceivedError(@Nullable KlarnaStandaloneWebView view, int errorCode, @Nullable String description, @Nullable String failingUrl) {
            // From API 23, WebView reports main-frame errors to this deprecated callback as well as
            // the one above, so only send the event here on older versions to send it once.
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
                klarnaStandaloneWebViewEventSender.sendNavigationErrorEvent(view, errorCode, description, failingUrl);
            }
        }

        @Override
        public void onProgressChanged(@Nullable KlarnaStandaloneWebView view, int newProgress) {
            klarnaStandaloneWebViewEventSender.sendLoadProgressEvent(view, newProgress);
        }

        @Override
        public void onRenderProcessGone(@Nullable KlarnaStandaloneWebView view, @Nullable RenderProcessGoneDetail detail) {
            boolean didCrash = false;
            if (detail != null && Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                didCrash = detail.didCrash();
            }
            klarnaStandaloneWebViewEventSender.sendRenderProcessGoneEvent(view, didCrash);
        }
    };

    public KlarnaStandaloneWebViewManager() {
        this(null);
    }

    KlarnaStandaloneWebViewManager(@Nullable KlarnaStandaloneWebViewEventSender eventSender) {
        super();
        klarnaStandaloneWebViewEventSender = eventSender != null
                ? eventSender
                : new KlarnaStandaloneWebViewEventSender(viewToReactContext);
    }

    @ReactProp(name = "returnUrl")
    @Override
    public void setReturnUrl(KlarnaStandaloneWebView view, @Nullable String returnUrl) {
        if (!Objects.equals(returnUrl, view.getReturnURL())) {
            view.setReturnURL(returnUrl);
        }
    }

    @ReactProp(name = "theme")
    @Override
    public void setTheme(KlarnaStandaloneWebView view, @Nullable String value) {
        if (value != null && !value.isEmpty()) {
            switch (value) {
                case "light":
                    view.setTheme(KlarnaTheme.LIGHT);
                    break;
                case "dark":
                    view.setTheme(KlarnaTheme.DARK);
                    break;
                case "automatic":
                    view.setTheme(KlarnaTheme.AUTOMATIC);
                    break;
            }
        }
    }

    @ReactProp(name = "overScrollMode")
    @Override
    public void setOverScrollMode(KlarnaStandaloneWebView view, String overScrollMode) {
        for (int i = 0; i < view.getChildCount(); i++) {
            View v = view.getChildAt(i);
            if (v instanceof WebView) {
                applyOverScrollMode(v, overScrollMode);
            }
        }
    }

    private void applyOverScrollMode(View view, String overScrollMode) {
        switch (overScrollMode) {
            case OVER_SCROLL_MODE_NEVER:
                view.setOverScrollMode(View.OVER_SCROLL_NEVER);
                break;
            case OVER_SCROLL_MODE_CONTENT:
                view.setOverScrollMode(View.OVER_SCROLL_IF_CONTENT_SCROLLS);
                break;
            case OVER_SCROLL_MODE_ALWAYS:
            default:
                view.setOverScrollMode(View.OVER_SCROLL_ALWAYS);
                break;
        }
    }

    @Override
    public void setBounces(KlarnaStandaloneWebView view, boolean value) {
        // No need to implement this as it is not supported in Android
    }

    @Override
    public void setSharedCookiesEnabled(KlarnaStandaloneWebView view, boolean value) {
        // CookieManager is already shared by every WebView in the app, so there is nothing to copy
    }

    @NonNull
    @Override
    public String getName() {
        return REACT_CLASS;
    }

    /**
     * Handles commands received from RN to a specific view.
     *
     * @param root      view receiving the command
     * @param commandId identifier of the command
     * @param args      array of command arguments
     */
    @Override
    public void receiveCommand(@NonNull KlarnaStandaloneWebView root, String commandId, @Nullable ReadableArray args) {
        switch (commandId) {
            case COMMAND_LOAD:
                if (args != null && args.size() > 0) {
                    String url = args.getString(0);
                    String optionsJson = args.size() > 1 ? args.getString(1) : null;
                    load(root, url, optionsJson);
                }
                break;
            case COMMAND_GO_FORWARD:
                goForward(root);
                break;
            case COMMAND_GO_BACK:
                goBack(root);
                break;
            case COMMAND_RELOAD:
                reload(root);
                break;
            case COMMAND_STOP_LOADING:
                stopLoading(root);
                break;
            case COMMAND_SET_COOKIES:
                if (args != null && args.size() > 1) {
                    setCookies(root, args.getInt(0), args.getString(1));
                }
                break;
        }
    }

    @Override
    public void load(KlarnaStandaloneWebView view, String url, @Nullable String optionsJson) {
        KlarnaStandaloneWebViewLoadOptions options = KlarnaStandaloneWebViewLoadOptions.parse(optionsJson);
        if (options.headers.isEmpty()) {
            view.loadUrl(url);
        } else {
            view.loadUrl(url, options.headers);
        }
    }

    @Override
    public void goForward(KlarnaStandaloneWebView view) {
        view.goForward();
    }

    @Override
    public void goBack(KlarnaStandaloneWebView view) {
        view.goBack();
    }

    @Override
    public void reload(KlarnaStandaloneWebView view) {
        view.reload();
    }

    @Override
    public void stopLoading(KlarnaStandaloneWebView view) {
        view.stopLoading();
    }

    // Stores the cookies in the app-wide CookieManager and reports once all of them are stored.
    @Override
    public void setCookies(KlarnaStandaloneWebView view, int requestId, String cookiesJson) {
        List<KlarnaStandaloneWebViewCookie> cookies;
        try {
            cookies = KlarnaStandaloneWebViewCookie.parseList(cookiesJson);
        } catch (JSONException e) {
            klarnaStandaloneWebViewEventSender.sendSetCookiesResultEvent(view, requestId, "Invalid cookies: " + e.getMessage());
            return;
        }
        if (cookies.isEmpty()) {
            klarnaStandaloneWebViewEventSender.sendSetCookiesResultEvent(view, requestId, "");
            return;
        }
        CookieManager cookieManager = CookieManager.getInstance();
        List<KlarnaStandaloneWebViewCookie.Expiry> expiries = new ArrayList<>();
        for (KlarnaStandaloneWebViewCookie cookie : cookies) {
            expiries.addAll(cookie.expiries);
        }
        // The old cookies are expired before the new ones are stored, so a new cookie is never expired by mistake.
        expireThenStore(cookieManager, view, requestId, cookies, expiries);
    }

    private void expireThenStore(CookieManager cookieManager, KlarnaStandaloneWebView view, int requestId,
                                 List<KlarnaStandaloneWebViewCookie> cookies,
                                 List<KlarnaStandaloneWebViewCookie.Expiry> expiries) {
        if (expiries.isEmpty()) {
            storeCookies(cookieManager, view, requestId, cookies);
            return;
        }
        int[] pending = {expiries.size()};
        for (KlarnaStandaloneWebViewCookie.Expiry expiry : expiries) {
            // A rejected expiry (for example a public suffix domain) only means there was nothing to remove.
            cookieManager.setCookie(expiry.url, expiry.setCookieValue, ignored -> {
                pending[0]--;
                if (pending[0] == 0) {
                    storeCookies(cookieManager, view, requestId, cookies);
                }
            });
        }
    }

    private void storeCookies(CookieManager cookieManager, KlarnaStandaloneWebView view, int requestId,
                              List<KlarnaStandaloneWebViewCookie> cookies) {
        List<String> failed = new ArrayList<>();
        int[] remaining = {cookies.size()};
        for (KlarnaStandaloneWebViewCookie cookie : cookies) {
            cookieManager.setCookie(cookie.url, cookie.setCookieValue, stored -> {
                if (!Boolean.TRUE.equals(stored)) {
                    failed.add(cookie.name);
                }
                remaining[0]--;
                if (remaining[0] == 0) {
                    cookieManager.flush();
                    String error = failed.isEmpty() ? "" : "Could not store the cookies " + TextUtils.join(", ", failed) + ".";
                    klarnaStandaloneWebViewEventSender.sendSetCookiesResultEvent(view, requestId, error);
                }
            });
        }
    }

    /**
     * Exposes direct event types that will be accessible as prop "callbacks" from RN.
     * <p>
     * Structure must follow:
     * { "<eventName>": {"registrationName": "<eventName>"} }
     */
    @Nullable
    @Override
    public Map<String, Object> getExportedCustomDirectEventTypeConstants() {
        MapBuilder.Builder<String, Object> builder = MapBuilder.builder();
        builder.put(KlarnaStandaloneWebViewEvent.Event.ON_LOAD_START.name, MapBuilder.of("registrationName", KlarnaStandaloneWebViewEvent.Event.ON_LOAD_START.name));
        builder.put(KlarnaStandaloneWebViewEvent.Event.ON_LOAD_END.name, MapBuilder.of("registrationName", KlarnaStandaloneWebViewEvent.Event.ON_LOAD_END.name));
        builder.put(KlarnaStandaloneWebViewEvent.Event.ON_ERROR.name, MapBuilder.of("registrationName", KlarnaStandaloneWebViewEvent.Event.ON_ERROR.name));
        builder.put(KlarnaStandaloneWebViewEvent.Event.ON_LOAD_PROGRESS.name, MapBuilder.of("registrationName", KlarnaStandaloneWebViewEvent.Event.ON_LOAD_PROGRESS.name));
        builder.put(KlarnaStandaloneWebViewEvent.Event.ON_KLARNA_MESSAGE.name, MapBuilder.of("registrationName", KlarnaStandaloneWebViewEvent.Event.ON_KLARNA_MESSAGE.name));
        builder.put(KlarnaStandaloneWebViewEvent.Event.ON_RENDER_PROCESS_GONE.name, MapBuilder.of("registrationName", KlarnaStandaloneWebViewEvent.Event.ON_RENDER_PROCESS_GONE.name));
        builder.put(KlarnaStandaloneWebViewEvent.Event.ON_SET_COOKIES_RESULT.name, MapBuilder.of("registrationName", KlarnaStandaloneWebViewEvent.Event.ON_SET_COOKIES_RESULT.name));
        return builder.build();
    }

    @NonNull
    @Override
    protected KlarnaStandaloneWebView createViewInstance(@NonNull ThemedReactContext themedReactContext) {
        KlarnaStandaloneWebView klarnaStandaloneWebView = new KlarnaStandaloneWebView(
                /* context */ themedReactContext.getCurrentActivity(), // needs to be activity for SDK dialogs to work
                /* attrs */ null,
                /* defStyleAttr */ 0,
                /* webViewClient */ klarnaStandaloneWebViewClient,
                /* eventHandler */ klarnaEventHandler,
                /* environment */ null,
                /* region */ null,
                /* theme */ null,
                /* resourceEndpoint */ null,
                /* returnURL */ null);
        viewToReactContext.put(klarnaStandaloneWebView, themedReactContext);
        return klarnaStandaloneWebView;
    }

    @Override
    public void onDropViewInstance(@NonNull KlarnaStandaloneWebView view) {
        viewToReactContext.remove(view);
        super.onDropViewInstance(view);
    }

}
