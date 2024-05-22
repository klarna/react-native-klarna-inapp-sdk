package com.klarna.mobile.sdk.reactnative.common.event;

import android.os.SystemClock;

import com.facebook.react.bridge.Arguments;
import com.facebook.react.bridge.JavaOnlyArray;
import com.facebook.react.bridge.JavaOnlyMap;
import com.facebook.react.bridge.WritableMap;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

@RunWith(PowerMockRunner.class)
@PrepareForTest({Arguments.class, SystemClock.class})
public class ComponentEventTest {

    @Before
    public void setup() {
        PowerMockito.mockStatic(Arguments.class);
        PowerMockito.when(Arguments.createArray())
                .thenAnswer(invocation -> {
                    return new JavaOnlyArray();
                });
        PowerMockito.when(Arguments.createMap())
                .thenAnswer(invocation -> {
                    return new JavaOnlyMap();
                });
        PowerMockito.mockStatic(SystemClock.class);
    }

    @After
    public void teardown() {

    }

    @Test
    public void testEventName() {
        // When
        ComponentEvent event = new ComponentEvent(1, "test", null);

        // Then
        Assert.assertEquals("test", event.getEventName());
    }

    @Test
    public void testEventData() {
        // Given
        WritableMap map = new JavaOnlyMap();
        map.putString("key", "value");

        // When
        ComponentEvent event = new ComponentEvent(1, "test", map);

        // Then
        Assert.assertEquals(map, event.getEventData());
    }

    @Test
    public void testEventDataNull() {
        // When
        ComponentEvent event = new ComponentEvent(1, "test", null);

        // Then
        Assert.assertNotNull(event.getEventData());
        Assert.assertFalse(event.getEventData().keySetIterator().hasNextKey());
    }
}
