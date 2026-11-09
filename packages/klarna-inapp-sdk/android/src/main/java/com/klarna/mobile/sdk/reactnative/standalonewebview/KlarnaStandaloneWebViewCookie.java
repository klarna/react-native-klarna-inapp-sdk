package com.klarna.mobile.sdk.reactnative.standalonewebview;

import androidx.annotation.NonNull;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

/**
 * A cookie to store for KlarnaStandaloneWebView, parsed from the JSON built and validated by the JS wrapper.
 */
final class KlarnaStandaloneWebViewCookie {

    private static final String KEY_NAME = "name";
    private static final String KEY_VALUE = "value";
    private static final String KEY_DOMAIN = "domain";
    private static final String KEY_PATH = "path";
    private static final String KEY_SECURE = "secure";
    private static final String KEY_HTTP_ONLY = "httpOnly";
    private static final String KEY_EXPIRES = "expires";
    private static final String KEY_HOST_ONLY = "hostOnly";
    private static final String KEY_REPLACE_CONFLICTING = "replaceConflicting";
    private static final String KEY_REQUEST_PATH = "requestPath";
    // Secure, because Chromium rejects an expiry without it for __Secure- and __Host- names; the URLs are https.
    private static final String EXPIRED = "; Max-Age=0; Expires=Thu, 01 Jan 1970 00:00:00 GMT; Secure";

    @NonNull
    final String name;
    /** The URL to pass to CookieManager.setCookie, whose host matches the cookie's domain. */
    @NonNull
    final String url;
    /** The cookie in Set-Cookie format. */
    @NonNull
    final String setCookieValue;

    /** The Set-Cookie values that expire same-name cookies this one would otherwise be sent alongside; empty unless replaceConflicting. */
    @NonNull
    final List<Expiry> expiries;

    /** An already expired cookie to hand to CookieManager.setCookie for the URL. */
    static final class Expiry {
        @NonNull
        final String url;
        @NonNull
        final String setCookieValue;

        Expiry(@NonNull String url, @NonNull String setCookieValue) {
            this.url = url;
            this.setCookieValue = setCookieValue;
        }
    }

    private KlarnaStandaloneWebViewCookie(@NonNull String name, @NonNull String url, @NonNull String setCookieValue,
                                          @NonNull List<Expiry> expiries) {
        this.name = name;
        this.url = url;
        this.setCookieValue = setCookieValue;
        this.expiries = expiries;
    }

    @NonNull
    static List<KlarnaStandaloneWebViewCookie> parseList(@NonNull String json) throws JSONException {
        JSONArray array = new JSONArray(json);
        List<KlarnaStandaloneWebViewCookie> cookies = new ArrayList<>();
        for (int i = 0; i < array.length(); i++) {
            cookies.add(parse(array.getJSONObject(i)));
        }
        return cookies;
    }

    @NonNull
    private static KlarnaStandaloneWebViewCookie parse(@NonNull JSONObject json) throws JSONException {
        String name = json.getString(KEY_NAME);
        String domain = json.getString(KEY_DOMAIN);
        String path = json.optString(KEY_PATH, "/");
        StringBuilder value = new StringBuilder().append(name).append('=').append(json.getString(KEY_VALUE));
        // The Domain attribute makes the cookie apply to subdomains too; without it the URL's host scopes it,
        // which __Host- cookies require.
        if (!json.optBoolean(KEY_HOST_ONLY, false)) {
            value.append("; Domain=").append(domain);
        }
        value.append("; Path=").append(path);
        if (json.has(KEY_EXPIRES)) {
            value.append("; Expires=").append(formatExpires(json.getLong(KEY_EXPIRES)));
        }
        if (json.optBoolean(KEY_SECURE, false)) {
            value.append("; Secure");
        }
        if (json.optBoolean(KEY_HTTP_ONLY, false)) {
            value.append("; HttpOnly");
        }
        List<Expiry> expiries = json.optBoolean(KEY_REPLACE_CONFLICTING, false)
                ? expiriesFor(name, domain, json.optString(KEY_REQUEST_PATH, "/"))
                : new ArrayList<>();
        return new KlarnaStandaloneWebViewCookie(name, "https://" + domain + path, value.toString(), expiries);
    }

    /**
     * CookieManager cannot list cookies, so each way a same-name cookie could reach a request to the path is
     * expired: at every path prefix, host-only and with a Domain attribute for the host and its parent domains.
     */
    @NonNull
    private static List<Expiry> expiriesFor(@NonNull String name, @NonNull String host, @NonNull String requestPath) {
        List<String> paths = new ArrayList<>();
        paths.add("/");
        for (int i = requestPath.indexOf('/', 1); i > 0; i = requestPath.indexOf('/', i + 1)) {
            paths.add(requestPath.substring(0, i));
            paths.add(requestPath.substring(0, i + 1));
        }
        // A cookie whose Path is the page itself also applies to it.
        paths.add(requestPath);
        List<String> domains = new ArrayList<>();
        for (String d = host; d.indexOf('.') > 0; d = d.substring(d.indexOf('.') + 1)) {
            domains.add(d);
        }
        List<Expiry> expiries = new ArrayList<>();
        for (String path : new java.util.LinkedHashSet<>(paths)) {
            String url = "https://" + host + path;
            expiries.add(new Expiry(url, name + "=; Path=" + path + EXPIRED));
            for (String domain : domains) {
                expiries.add(new Expiry(url, name + "=; Domain=" + domain + "; Path=" + path + EXPIRED));
            }
        }
        return expiries;
    }

    @NonNull
    private static String formatExpires(long epochMillis) {
        SimpleDateFormat format = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss 'GMT'", Locale.US);
        format.setTimeZone(TimeZone.getTimeZone("GMT"));
        return format.format(new Date(epochMillis));
    }
}
