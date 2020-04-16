package com.klarna.inapp.sdk;

import android.os.SystemClock;

import com.facebook.react.bridge.Arguments;
import com.facebook.react.bridge.JavaOnlyArray;
import com.facebook.react.bridge.JavaOnlyMap;
import com.klarna.mobile.sdk.api.payments.KlarnaPaymentsSDKError;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PowerMockIgnore;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.rule.PowerMockRule;
import org.robolectric.RobolectricTestRunner;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;

@PrepareForTest({Arguments.class, SystemClock.class})
@PowerMockIgnore({"org.mockito.*", "org.robolectric.*", "androidx.*", "android.*", "com.android.*"})
@RunWith(RobolectricTestRunner.class)
public class MappableKlarnaPaymentsSDKErrorTest {

    @Rule
    public PowerMockRule rule = new PowerMockRule();

    @Before
    public void setup() {
        PowerMockito.mockStatic(Arguments.class);
        PowerMockito.when(Arguments.createArray())
                .thenAnswer(
                        new Answer<Object>() {
                            @Override
                            public Object answer(InvocationOnMock invocation) throws Throwable {
                                return new JavaOnlyArray();
                            }
                        });
        PowerMockito.when(Arguments.createMap())
                .thenAnswer(
                        new Answer<Object>() {
                            @Override
                            public Object answer(InvocationOnMock invocation) throws Throwable {
                                return new JavaOnlyMap();
                            }
                        });
        PowerMockito.mockStatic(SystemClock.class);
    }


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
