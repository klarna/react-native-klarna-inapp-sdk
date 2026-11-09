package com.klarna.mobile.sdk.reactnative.standalonewebview;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;

@RunWith(RobolectricTestRunner.class)
public class KlarnaStandaloneWebViewEventTest {

    @Test
    public void testSetCookiesResultIsNeverCoalesced() {
        KlarnaStandaloneWebViewEvent event = new KlarnaStandaloneWebViewEvent(
                1, KlarnaStandaloneWebViewEvent.Event.ON_SET_COOKIES_RESULT.name, null);

        Assert.assertFalse(event.canCoalesce());
    }

    @Test
    public void testOtherEventsKeepDefaultCoalescing() {
        KlarnaStandaloneWebViewEvent event = new KlarnaStandaloneWebViewEvent(
                1, KlarnaStandaloneWebViewEvent.Event.ON_LOAD_END.name, null);

        Assert.assertTrue(event.canCoalesce());
    }
}
