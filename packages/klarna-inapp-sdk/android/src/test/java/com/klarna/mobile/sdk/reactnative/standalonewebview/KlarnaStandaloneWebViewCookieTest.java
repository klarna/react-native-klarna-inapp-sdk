package com.klarna.mobile.sdk.reactnative.standalonewebview;

import org.json.JSONException;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;

import java.util.List;

@RunWith(RobolectricTestRunner.class)
public class KlarnaStandaloneWebViewCookieTest {

    @Test
    public void testCookieWithDefaults() throws JSONException {
        List<KlarnaStandaloneWebViewCookie> cookies = KlarnaStandaloneWebViewCookie.parseList(
                "[{\"name\":\"session\",\"value\":\"abc\",\"domain\":\"www.example.com\",\"path\":\"/\"}]");

        Assert.assertEquals(1, cookies.size());
        Assert.assertEquals("session", cookies.get(0).name);
        Assert.assertEquals("https://www.example.com/", cookies.get(0).url);
        Assert.assertEquals("session=abc; Domain=www.example.com; Path=/", cookies.get(0).setCookieValue);
    }

    @Test
    public void testCookieAttributes() throws JSONException {
        List<KlarnaStandaloneWebViewCookie> cookies = KlarnaStandaloneWebViewCookie.parseList(
                "[{\"name\":\"token\",\"value\":\"t\",\"domain\":\"example.com\",\"path\":\"/checkout\","
                        + "\"secure\":true,\"httpOnly\":true,\"expires\":1893456000000}]");

        Assert.assertEquals("https://example.com/checkout", cookies.get(0).url);
        Assert.assertEquals(
                "token=t; Domain=example.com; Path=/checkout; Expires=Tue, 01 Jan 2030 00:00:00 GMT; Secure; HttpOnly",
                cookies.get(0).setCookieValue);
    }

    @Test
    public void testParsesCookiesInOrder() throws JSONException {
        List<KlarnaStandaloneWebViewCookie> cookies = KlarnaStandaloneWebViewCookie.parseList(
                "[{\"name\":\"a\",\"value\":\"1\",\"domain\":\"example.com\"},"
                        + "{\"name\":\"b\",\"value\":\"2\",\"domain\":\"example.com\"}]");

        Assert.assertEquals("a", cookies.get(0).name);
        Assert.assertEquals("b", cookies.get(1).name);
        Assert.assertEquals("a=1; Domain=example.com; Path=/", cookies.get(0).setCookieValue);
    }

    @Test
    public void testHostOnlyCookieHasNoDomainAttribute() throws JSONException {
        List<KlarnaStandaloneWebViewCookie> cookies = KlarnaStandaloneWebViewCookie.parseList(
                "[{\"name\":\"__Host-session\",\"value\":\"abc\",\"domain\":\"checkout.example.com\",\"path\":\"/\","
                        + "\"secure\":true,\"hostOnly\":true}]");

        Assert.assertEquals("https://checkout.example.com/", cookies.get(0).url);
        Assert.assertEquals("__Host-session=abc; Path=/; Secure", cookies.get(0).setCookieValue);
    }

    @Test(expected = JSONException.class)
    public void testCookieWithoutDomainIsInvalid() throws JSONException {
        KlarnaStandaloneWebViewCookie.parseList("[{\"name\":\"a\",\"value\":\"1\"}]");
    }

    @Test(expected = JSONException.class)
    public void testInvalidJsonIsInvalid() throws JSONException {
        KlarnaStandaloneWebViewCookie.parseList("{not json");
    }
}
