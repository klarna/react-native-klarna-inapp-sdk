package com.klarna.mobile.sdk.reactnative.klarnanetwork.messaging

import com.facebook.react.bridge.Arguments
import com.facebook.react.bridge.JavaOnlyMap
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.mockito.MockedStatic
import org.mockito.Mockito

class KlarnaMessagingPlacementEventsTest {

    private lateinit var mockedArguments: MockedStatic<Arguments>

    @Before
    fun setUp() {
        mockedArguments = Mockito.mockStatic(Arguments::class.java)
        mockedArguments.`when`<JavaOnlyMap> { Arguments.createMap() }
            .thenAnswer { JavaOnlyMap() }
    }

    @After
    fun tearDown() {
        mockedArguments.close()
    }

    @Test
    fun resizedEvent_eventName() {
        val event = KlarnaMessagingPlacementResizedEvent(0, 1, "120")
        assertEquals("onResized", event.eventName)
    }

    @Test
    fun errorEvent_eventName() {
        val event = KlarnaMessagingPlacementErrorEvent(0, 1, "TestError", "Something went wrong")
        assertEquals("onError", event.eventName)
    }
}
