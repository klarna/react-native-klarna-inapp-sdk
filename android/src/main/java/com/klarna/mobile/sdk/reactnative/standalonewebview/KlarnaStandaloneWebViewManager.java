package com.klarna.mobile.sdk.reactnative.standalonewebview;

import android.app.Application;
import android.graphics.Bitmap;
import android.os.Build;
import android.webkit.RenderProcessGoneDetail;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContext;
import com.facebook.react.bridge.ReadableArray;
import com.facebook.react.common.MapBuilder;
import com.facebook.react.uimanager.ThemedReactContext;
import com.facebook.react.uimanager.UIManagerHelper;
import com.facebook.react.uimanager.annotations.ReactProp;
import com.facebook.react.uimanager.events.EventDispatcher;
import com.klarna.mobile.sdk.KlarnaMobileSDKError;
import com.klarna.mobile.sdk.api.KlarnaEventHandler;
import com.klarna.mobile.sdk.api.KlarnaProductEvent;
import com.klarna.mobile.sdk.api.component.KlarnaComponent;
import com.klarna.mobile.sdk.api.standalonewebview.KlarnaStandaloneWebView;
import com.klarna.mobile.sdk.api.standalonewebview.KlarnaStandaloneWebViewClient;
import com.klarna.mobile.sdk.reactnative.spec.RNKlarnaStandaloneWebViewSpec;

import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class KlarnaStandaloneWebViewManager extends RNKlarnaStandaloneWebViewSpec<KlarnaStandaloneWebView> {

    // Commands that can be triggered from React Native side
    public static final String COMMAND_LOAD = "load";
    public static final String COMMAND_GO_FORWARD = "goForward";
    public static final String COMMAND_GO_BACK = "goBack";
    public static final String COMMAND_RELOAD = "reload";

    private static final String REACT_CLASS = "RNKlarnaStandaloneWebView";

    // Store a list of views to event dispatchers so we send up events via the right views.
    private final Map<WeakReference<KlarnaStandaloneWebView>, EventDispatcher> viewToDispatcher;
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
    private final KlarnaStandaloneWebViewClient klarnaStandaloneWebViewClient = new KlarnaStandaloneWebViewClient() {
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
            if (error != null && Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                klarnaStandaloneWebViewEventSender.sendErrorEvent(view, error.getErrorCode(), error.getDescription().toString());
            }
        }

        @Override
        public void onReceivedError(@Nullable KlarnaStandaloneWebView view, int errorCode, @Nullable String description, @Nullable String failingUrl) {
            klarnaStandaloneWebViewEventSender.sendErrorEvent(view, errorCode, description);
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

    public KlarnaStandaloneWebViewManager(ReactApplicationContext reactContext, Application applicationContext) {
        super();
        viewToDispatcher = new HashMap<>();
        klarnaStandaloneWebViewEventSender = new KlarnaStandaloneWebViewEventSender(viewToDispatcher);
    }

    @ReactProp(name = "returnUrl")
    @Override
    public void setReturnUrl(KlarnaStandaloneWebView view, @Nullable String returnUrl) {
        if (!Objects.equals(returnUrl, view.getReturnURL())) {
            view.setReturnURL(returnUrl);
        }
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
                    load(root, url);
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
        }
    }

    @Override
    public void load(KlarnaStandaloneWebView view, String url) {
        view.loadUrl(url);
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
        return builder.build();
    }

    @NonNull
    @Override
    protected KlarnaStandaloneWebView createViewInstance(@NonNull ThemedReactContext themedReactContext) {
        KlarnaStandaloneWebView klarnaStandaloneWebView = new KlarnaStandaloneWebView(
                /* context */ themedReactContext,
                /* attrs */ null,
                /* defStyleAttr */ 0,
                /* webViewClient */ klarnaStandaloneWebViewClient,
                /* eventHandler */ klarnaEventHandler,
                /* environment */ null,
                /* region */ null,
                /* theme */ null,
                /* resourceEndpoint */ null,
                /* returnURL */ null);
        // Each view has its own event dispatcher.
        EventDispatcher dispatcher = UIManagerHelper.getEventDispatcherForReactTag((ReactContext) klarnaStandaloneWebView.getContext(), klarnaStandaloneWebView.getId());
        viewToDispatcher.put(new WeakReference<>(klarnaStandaloneWebView), dispatcher);
        return klarnaStandaloneWebView;
    }

}
