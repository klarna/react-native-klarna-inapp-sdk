package com.klarna.mobile.sdk.reactnative.klarnanetwork.messaging

import com.facebook.react.bridge.Arguments
import com.facebook.react.bridge.JavaOnlyMap
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.mockito.MockedStatic
import org.mockito.Mockito

class KlarnaMessagingPlacementViewManagerTest {

    private lateinit var manager: KlarnaMessagingPlacementViewManager
    private lateinit var mockedArguments: MockedStatic<Arguments>

    @Before
    fun setUp() {
        manager = KlarnaMessagingPlacementViewManager()
        mockedArguments = Mockito.mockStatic(Arguments::class.java)
        mockedArguments.`when`<JavaOnlyMap> { Arguments.createMap() }
            .thenAnswer { JavaOnlyMap() }
    }

    @After
    fun tearDown() {
        mockedArguments.close()
    }

    // MARK: - getName

    @Test
    fun getName_returnsCorrectReactClass() {
        assertEquals("RNKlarnaMessagingPlacementView", manager.name)
    }

    // MARK: - getExportedCustomDirectEventTypeConstants

    @Test
    fun eventConstants_containsOnError() {
        val constants = manager.exportedCustomDirectEventTypeConstants
        assertNotNull(constants)
        assertTrue(constants!!.containsKey("onError"))
    }

    @Test
    fun eventConstants_containsOnResized() {
        val constants = manager.exportedCustomDirectEventTypeConstants
        assertNotNull(constants)
        assertTrue(constants!!.containsKey("onResized"))
    }

    @Test
    fun eventConstants_onErrorRegistrationName() {
        val constants = manager.exportedCustomDirectEventTypeConstants!!
        @Suppress("UNCHECKED_CAST")
        val onError = constants["onError"] as Map<String, Any>
        assertEquals("onError", onError["registrationName"])
    }

    @Test
    fun eventConstants_onResizedRegistrationName() {
        val constants = manager.exportedCustomDirectEventTypeConstants!!
        @Suppress("UNCHECKED_CAST")
        val onResized = constants["onResized"] as Map<String, Any>
        assertEquals("onResized", onResized["registrationName"])
    }

    @Test
    fun eventConstants_hasExactlyTwoEntries() {
        val constants = manager.exportedCustomDirectEventTypeConstants!!
        assertEquals(2, constants.size)
    }
}
