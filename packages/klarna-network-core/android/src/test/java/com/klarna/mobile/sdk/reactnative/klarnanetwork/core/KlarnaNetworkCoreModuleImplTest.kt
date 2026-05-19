package com.klarna.mobile.sdk.reactnative.klarnanetwork.core

import com.facebook.react.bridge.Promise
import com.facebook.react.bridge.ReactApplicationContext
import com.facebook.react.bridge.ReadableArray
import com.facebook.react.bridge.ReadableMap
import com.klarna.mobile.sdk.klarna.network.core.api.common.KlarnaResult
import com.klarna.mobile.sdk.klarna.network.core.api.common.KlarnaSDKError
import com.klarna.mobile.sdk.klarna.network.core.api.klarna.Klarna
import com.klarna.mobile.sdk.klarna.network.core.api.klarna.KlarnaConfiguration
import io.mockk.Runs
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import io.mockk.slot
import io.mockk.unmockkAll
import io.mockk.verify
import org.junit.After
import org.junit.Before
import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class KlarnaNetworkCoreModuleImplTest {

    private val errorInstanceNotFound = "No instance found for the given instanceId. Call initialize first."

    private val mockContext = mockk<ReactApplicationContext>(relaxed = true)
    private val mockPromise = mockk<Promise>(relaxed = true)
    private val mockKlarna = mockk<Klarna>(relaxed = true)

    private lateinit var impl: KlarnaNetworkCoreModuleImpl

    @Before
    fun setUp() {
        impl = KlarnaNetworkCoreModuleImpl(mockContext)
    }

    @After
    fun tearDown() {
        KlarnaNetworkCoreModuleImpl.instances.clear()
        unmockkAll()
    }

    // region Helpers

    private fun configMap(
        clientId: String? = "client_123",
        accountId: String? = null,
        locale: String? = null,
        klarnaNetworkSessionToken: String? = null
    ): ReadableMap = mockk {
        every { getString("clientId") } returns clientId
        every { getString("accountId") } returns accountId
        every { getString("locale") } returns locale
        every { getString("klarnaNetworkSessionToken") } returns klarnaNetworkSessionToken
    }

    private fun integratorMap(
        name: String = "MyApp",
        sessionReference: String = "ref_123",
        moduleName: String? = null,
        moduleVersion: String? = null
    ): ReadableMap = mockk {
        every { getString("name") } returns name
        every { getString("sessionReference") } returns sessionReference
        every { getString("moduleName") } returns moduleName
        every { getString("moduleVersion") } returns moduleVersion
    }

    private fun metadataMap(
        integrator: ReadableMap,
        originators: ReadableArray? = null
    ): ReadableMap = mockk {
        every { getMap("integrator") } returns integrator
        every { getArray("originators") } returns originators
    }

    private fun givenInitializeSucceeds(instanceId: String = "inst_1") {
        KlarnaNetworkCoreModuleImpl.instances[instanceId] = mockKlarna
    }

    // endregion

    // region initialize

    @Test
    fun `initialize - rejects when clientId is missing`() {
        impl.initialize("inst_1", configMap(clientId = null), mockPromise)
        verify { mockPromise.reject(KlarnaNetworkCoreModuleImpl.NAME, "clientId is required") }
    }

    @Test
    fun `initialize - resolves on success`() {
        impl = KlarnaNetworkCoreModuleImpl(mockContext) { _, _ -> KlarnaResult.Success(mockKlarna) }
        impl.initialize("inst_1", configMap(), mockPromise)
        verify { mockPromise.resolve(isNull()) }
    }

    @Test
    fun `initialize - builds correct KlarnaConfiguration`() {
        var capturedConfig: KlarnaConfiguration? = null
        impl = KlarnaNetworkCoreModuleImpl(mockContext) { _, cfg ->
            capturedConfig = cfg
            KlarnaResult.Success(mockKlarna)
        }
        impl.initialize(
            "inst_1",
            configMap(
                clientId = "client_123",
                accountId = "account_456",
                locale = "en-US",
                klarnaNetworkSessionToken = "token_789"
            ),
            mockPromise
        )
        assertEquals("client_123", capturedConfig?.clientId)
        assertEquals("account_456", capturedConfig?.accountId)
        assertEquals("en-US", capturedConfig?.locale)
        assertEquals("token_789", capturedConfig?.klarnaNetworkSessionToken)
    }

    @Test
    fun `initialize - rejects on native failure`() {
        val mockError = mockk<KlarnaSDKError> {
            every { name } returns "Core error name"
            every { message } returns "Init failed"
        }
        impl = KlarnaNetworkCoreModuleImpl(mockContext) { _, _ -> KlarnaResult.Failure(mockError) }
        impl.initialize("inst_1", configMap(), mockPromise)
        verify { mockPromise.reject("Core error name", "Init failed") }
    }

    // endregion

    // region getSessionToken

    @Test
    fun `getSessionToken - rejects when instance not found`() {
        impl.getSessionToken("unknown_id", mockPromise)
        verify {
            mockPromise.reject(
                KlarnaNetworkCoreModuleImpl.NAME,
                errorInstanceNotFound
            )
        }
    }

    @Test
    fun `getSessionToken - resolves with token on success`() {
        givenInitializeSucceeds()
        every { mockKlarna.network.session.token(any()) } answers {
            firstArg<(KlarnaResult<String, KlarnaSDKError>) -> Unit>().invoke(KlarnaResult.Success("tok_123"))
        }
        impl.getSessionToken("inst_1", mockPromise)
        verify { mockPromise.resolve("tok_123") }
    }

    @Test
    fun `getSessionToken - rejects on native failure`() {
        givenInitializeSucceeds()
        val mockError = mockk<KlarnaSDKError> {
            every { name } returns "Core error name"
            every { message } returns "Token failed"
        }
        every { mockKlarna.network.session.token(any()) } answers {
            firstArg<(KlarnaResult<String, KlarnaSDKError>) -> Unit>().invoke(KlarnaResult.Failure(mockError))
        }
        impl.getSessionToken("inst_1", mockPromise)
        verify { mockPromise.reject("Core error name", "Token failed") }
    }

    // endregion

    // region clearSession

    @Test
    fun `clearSession - rejects when instance not found`() {
        impl.clearSession("unknown_id", mockPromise)
        verify {
            mockPromise.reject(
                KlarnaNetworkCoreModuleImpl.NAME,
                errorInstanceNotFound
            )
        }
    }

    @Test
    fun `clearSession - resolves on success`() {
        givenInitializeSucceeds()
        every { mockKlarna.network.session.clear(any()) } answers {
            firstArg<(KlarnaResult<Unit, KlarnaSDKError>) -> Unit>().invoke(KlarnaResult.Success(Unit))
        }
        impl.clearSession("inst_1", mockPromise)
        verify { mockPromise.resolve(isNull()) }
    }

    @Test
    fun `clearSession - rejects on native failure`() {
        givenInitializeSucceeds()
        val mockError = mockk<KlarnaSDKError> {
            every { name } returns "Core error name"
            every { message } returns "Clear failed"
        }
        every { mockKlarna.network.session.clear(any()) } answers {
            firstArg<(KlarnaResult<Unit, KlarnaSDKError>) -> Unit>().invoke(KlarnaResult.Failure(mockError))
        }
        impl.clearSession("inst_1", mockPromise)
        verify { mockPromise.reject("Core error name", "Clear failed") }
    }

    // endregion

    // region handleReturnUrl

    @Test
    fun `handleReturnUrl - rejects not supported on Android`() {
        givenInitializeSucceeds()
        impl.handleReturnUrl("app://return", mockPromise)
        verify { mockPromise.resolve(false) }
    }

    // endregion

    // region setIntegrationMetadata

    @Test
    fun `setIntegrationMetadata - does nothing when instance not found`() {
        impl.setIntegrationMetadata("unknown_id", metadataMap(integratorMap()))
        // no exception, no interaction with any promise
    }

    @Test
    fun `setIntegrationMetadata - sets integrationMetadata on klarna instance`() {
        givenInitializeSucceeds()
        impl.setIntegrationMetadata("inst_1", metadataMap(integratorMap()))
        verify { mockKlarna.integrationMetadata = any() }
    }

    @Test
    fun `setIntegrationMetadata - maps integrator fields correctly`() {
        givenInitializeSucceeds()
        val metadataSlot = slot<com.klarna.mobile.sdk.klarna.network.core.api.metadata.KlarnaIntegrationMetadata>()
        every { mockKlarna.integrationMetadata = capture(metadataSlot) } just Runs
        impl.setIntegrationMetadata(
            "inst_1",
            metadataMap(
                integratorMap(
                    name = "MyApp",
                    sessionReference = "ref_123",
                    moduleName = "react-native-klarna",
                    moduleVersion = "1.0.0"
                )
            )
        )
        assertEquals("MyApp", metadataSlot.captured.integrator.name)
        assertEquals("ref_123", metadataSlot.captured.integrator.sessionReference)
        assertEquals("react-native-klarna", metadataSlot.captured.integrator.moduleName)
        assertEquals("1.0.0", metadataSlot.captured.integrator.moduleVersion)
    }

    // endregion

    // region dispose

    @Test
    fun `dispose - resolves when instance found`() {
        givenInitializeSucceeds()
        impl.dispose("inst_1", mockPromise)
        verify { mockPromise.resolve(isNull()) }
    }

    @Test
    fun `dispose - resolves even when instance not found (idempotent)`() {
        impl.dispose("unknown_id", mockPromise)
        verify { mockPromise.resolve(isNull()) }
    }

    @Test
    fun `dispose - removes instance so subsequent calls find no instance`() {
        givenInitializeSucceeds()
        impl.dispose("inst_1", mockk(relaxed = true))
        impl.getSessionToken("inst_1", mockPromise)
        verify {
            mockPromise.reject(
                KlarnaNetworkCoreModuleImpl.NAME,
                errorInstanceNotFound
            )
        }
    }

    // endregion

    // region getInstance

    @Test
    fun `getInstance - returns instance when found`() {
        givenInitializeSucceeds()
        assertEquals(mockKlarna, KlarnaNetworkCoreModuleImpl.getInstance("inst_1"))
    }

    @Test
    fun `getInstance - returns null when not found`() {
        assertNull(KlarnaNetworkCoreModuleImpl.getInstance("unknown_id"))
    }

    // endregion
}
