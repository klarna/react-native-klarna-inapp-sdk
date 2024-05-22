package com.klarna.mobile.sdk.reactnative.common.event;

import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.facebook.react.bridge.ReactContext;
import com.facebook.react.bridge.WritableMap;
import com.facebook.react.common.MapBuilder;
import com.facebook.react.uimanager.UIManagerHelper;
import com.facebook.react.uimanager.events.EventDispatcher;
import com.klarna.mobile.sdk.reactnative.common.ui.WrapperView;

import java.lang.ref.WeakReference;
import java.util.Collection;
import java.util.Map;

public abstract class ComponentEventSender<T extends View> {
    private final Map<WeakReference<T>, EventDispatcher> viewToDispatcher;

    protected ComponentEventSender(@NonNull final Map<WeakReference<T>, EventDispatcher> viewToDispatcher) {
        this.viewToDispatcher = viewToDispatcher;
    }

    protected void postEvent(@Nullable EventDispatcher eventDispatcher, @NonNull ComponentEvent event) {
        if (eventDispatcher != null) {
            eventDispatcher.dispatchEvent(event);
        }
    }

    protected ComponentEvent createEvent(@Nullable View view, @NonNull String eventName, @Nullable WritableMap params) {
        T viewReference = getView(view);
        if (viewReference == null) {
            return null;
        }
        return new ComponentEvent(viewReference.getId(), eventName, params);
    }

    protected void postEventForView(@Nullable View view, @NonNull String eventName, @Nullable WritableMap params) {
        T viewReference = getView(view);
        if (viewReference != null) {
            EventDispatcher eventDispatcher = UIManagerHelper.getEventDispatcherForReactTag((ReactContext) viewReference.getContext(), viewReference.getId());
            postEvent(eventDispatcher, createEvent(viewReference, eventName, params));
        }
    }

    @Nullable
    private T getView(@Nullable View view) {
        if (view == null) {
            return null;
        }
        for (WeakReference<T> reference : viewToDispatcher.keySet()) {
            T viewReference = reference.get();
            if (viewReference != null && viewReference == view) {
                return viewReference;
            }
            if (viewReference instanceof WrapperView) {
                View wrappedView = ((WrapperView) viewReference).getView();
                if (wrappedView == view) {
                    return viewReference;
                }
            }
        }
        return null;
    }

    public Map<String, Object> getExportedCustomDirectEventTypeConstants() {
        MapBuilder.Builder<String, Object> builder = MapBuilder.builder();
        Collection<String> eventNames = getCallbackEventNames();
        for (String eventName : eventNames) {
            builder.put(eventName, MapBuilder.of("registrationName", eventName));
        }
        return builder.build();
    }

    public abstract Collection<String> getCallbackEventNames();
}
