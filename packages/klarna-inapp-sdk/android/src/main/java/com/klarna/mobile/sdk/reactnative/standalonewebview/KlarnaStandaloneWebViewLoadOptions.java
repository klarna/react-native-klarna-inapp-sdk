package com.klarna.mobile.sdk.reactnative.standalonewebview;

import android.text.TextUtils;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * Headers sent with a KlarnaStandaloneWebView load, parsed from the JSON built by the JS wrapper.
 */
final class KlarnaStandaloneWebViewLoadOptions {

    private static final String TAG = "KlarnaStandaloneWebView";

    private static final String KEY_HEADERS = "headers";

    private static final KlarnaStandaloneWebViewLoadOptions EMPTY =
            new KlarnaStandaloneWebViewLoadOptions(Collections.emptyMap());

    @NonNull
    final Map<String, String> headers;

    private KlarnaStandaloneWebViewLoadOptions(@NonNull Map<String, String> headers) {
        this.headers = headers;
    }

    @NonNull
    static KlarnaStandaloneWebViewLoadOptions parse(@Nullable String json) {
        if (TextUtils.isEmpty(json)) {
            return EMPTY;
        }
        try {
            return new KlarnaStandaloneWebViewLoadOptions(parseHeaders(new JSONObject(json).optJSONObject(KEY_HEADERS)));
        } catch (JSONException e) {
            Log.e(TAG, "Ignoring invalid load options: " + e.getMessage());
            return EMPTY;
        }
    }

    @NonNull
    private static Map<String, String> parseHeaders(@Nullable JSONObject json) throws JSONException {
        Map<String, String> headers = new HashMap<>();
        if (json == null) {
            return headers;
        }
        Iterator<String> names = json.keys();
        while (names.hasNext()) {
            String name = names.next();
            headers.put(name, json.getString(name));
        }
        return headers;
    }
}
