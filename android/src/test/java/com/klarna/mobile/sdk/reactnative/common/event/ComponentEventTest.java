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
import org.mockito.MockedStatic;
import org.mockito.Mockito;

public class ComponentEventTest {

    private MockedStatic<Arguments> mockedStaticArguments;
    private MockedStatic<SystemClock> mockedStaticSystemClock;

    @Before
    public void setup() {
        mockedStaticArguments = Mockito.mockStatic(Arguments.class);
        Mockito.when(Arguments.createArray())
                .thenAnswer(invocation -> {
                    return new JavaOnlyArray();
                });
        Mockito.when(Arguments.createMap())
                .thenAnswer(invocation -> {
                    return new JavaOnlyMap();
                });
        mockedStaticSystemClock = Mockito.mockStatic(SystemClock.class);
    }

    @After
    public void teardown() {
        mockedStaticArguments.close();
        mockedStaticSystemClock.close();
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
