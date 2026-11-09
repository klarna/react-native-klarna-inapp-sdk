package com.klarna.mobile.sdk.reactnative.standalonewebview;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;

import java.util.HashMap;
import java.util.Map;

@RunWith(RobolectricTestRunner.class)
public class KlarnaStandaloneWebViewLoadOptionsTest {

    @Test
    public void testNullJsonHasNoHeaders() {
        Assert.assertTrue(KlarnaStandaloneWebViewLoadOptions.parse(null).headers.isEmpty());
    }

    @Test
    public void testEmptyJsonHasNoHeaders() {
        Assert.assertTrue(KlarnaStandaloneWebViewLoadOptions.parse("").headers.isEmpty());
    }

    @Test
    public void testInvalidJsonHasNoHeaders() {
        Assert.assertTrue(KlarnaStandaloneWebViewLoadOptions.parse("{not json").headers.isEmpty());
    }

    @Test
    public void testParsesHeaders() {
        KlarnaStandaloneWebViewLoadOptions options = KlarnaStandaloneWebViewLoadOptions.parse(
                "{\"headers\":{\"X-Store\":\"42\",\"Accept-Language\":\"en-US\"}}");

        Map<String, String> expected = new HashMap<>();
        expected.put("X-Store", "42");
        expected.put("Accept-Language", "en-US");
        Assert.assertEquals(expected, options.headers);
    }

    @Test
    public void testIgnoresOtherKeys() {
        KlarnaStandaloneWebViewLoadOptions options = KlarnaStandaloneWebViewLoadOptions.parse(
                "{\"cookies\":[{\"name\":\"a\",\"value\":\"1\"}]}");

        Assert.assertTrue(options.headers.isEmpty());
    }
}
