package com.klarna.inapp.sdk;

import com.klarna.mobile.sdk.api.payments.KlarnaPaymentsSDKError;

import org.junit.Assert;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;

public class MappableKlarnaPaymentsSDKErrorTest {

    @Test
    public void testMapFieldsAndValues() {
        KlarnaPaymentsSDKError error = new KlarnaPaymentsSDKError(
                "testName",
                "testMessage",
                false,
                "testAction",
                Collections.singletonList("testInvalidField")
        );

        MappableKlarnaPaymentsSDKError writableError = new MappableKlarnaPaymentsSDKError(error);

        Map map = writableError.buildMap();

        Assert.assertEquals("testName", map.get("name"));
        Assert.assertEquals("testMessage", map.get("message"));
        Assert.assertEquals(false, map.get("isFatal"));
        Assert.assertEquals("testAction", map.get("action"));

        Object inv = map.get("invalidFields");
        Assert.assertTrue(inv instanceof Collection);
        Collection invalidFields = (Collection) inv;

        Assert.assertNotNull(invalidFields);
        Assert.assertEquals(1, invalidFields.size());
        Assert.assertEquals("testInvalidField", new ArrayList(invalidFields).get(0));

    }
}
