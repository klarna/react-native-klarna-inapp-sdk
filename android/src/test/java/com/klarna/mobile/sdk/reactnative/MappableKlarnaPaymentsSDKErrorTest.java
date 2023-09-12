package com.klarna.mobile.sdk.reactnative;

import com.facebook.react.bridge.Arguments;
import com.facebook.react.bridge.JavaOnlyArray;
import com.facebook.react.bridge.JavaOnlyMap;
import com.facebook.react.bridge.ReadableArray;
import com.facebook.react.bridge.ReadableMap;
import com.klarna.mobile.sdk.api.payments.KlarnaPaymentsSDKError;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PowerMockIgnore;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.rule.PowerMockRule;
import org.robolectric.RobolectricTestRunner;

import java.util.Collections;

@PrepareForTest({Arguments.class})
@RunWith(RobolectricTestRunner.class)
@PowerMockIgnore({"org.mockito.*", "org.robolectric.*", "androidx.*", "android.*", "com.android.*"})
public class MappableKlarnaPaymentsSDKErrorTest {

    @Rule
    public PowerMockRule rule = new PowerMockRule();

    @Before
    public void setup() {
        PowerMockito.mockStatic(Arguments.class);
        PowerMockito.when(Arguments.createArray()).thenAnswer(invocation -> new JavaOnlyArray());
        PowerMockito.when(Arguments.createMap()).thenAnswer(invocation -> new JavaOnlyMap());
    }

    @After
    public void teardown() {

    }

    @Test
    public void testMapFieldsAndValues() {
        KlarnaPaymentsSDKError error = new KlarnaPaymentsSDKError(
                "testName",
                "testMessage",
                false,
                "testAction",
                Collections.singletonList("testInvalidField"),
                "testSessionId"
        );

        MappableKlarnaPaymentsSDKError writableError = new MappableKlarnaPaymentsSDKError(error);

        ReadableMap map = writableError.buildMap();

        Assert.assertEquals("testName", map.getString("name"));
        Assert.assertEquals("testMessage", map.getString("message"));
        Assert.assertFalse(map.getBoolean("isFatal"));
        Assert.assertEquals("testAction", map.getString("action"));

        ReadableArray invalidFields = map.getArray("invalidFields");
        Assert.assertNotNull(invalidFields);
        Assert.assertEquals(1, invalidFields.size());
        Assert.assertEquals("testInvalidField", invalidFields.getString(0));

        Assert.assertEquals("testSessionId", map.getString("sessionId"));
    }
}
