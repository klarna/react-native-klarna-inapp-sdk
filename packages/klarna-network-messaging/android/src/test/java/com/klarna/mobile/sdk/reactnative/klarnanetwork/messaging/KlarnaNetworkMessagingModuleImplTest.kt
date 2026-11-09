package com.klarna.mobile.sdk.reactnative.klarnanetwork.messaging

import android.app.Activity
import android.os.Handler
import com.facebook.react.bridge.Arguments
import com.facebook.react.bridge.Promise
import com.facebook.react.bridge.ReactApplicationContext
import com.facebook.react.bridge.ReadableMap
import com.facebook.react.bridge.ReadableType
import com.facebook.react.bridge.WritableMap
import com.klarna.mobile.sdk.klarna.network.core.api.common.KlarnaResult
import com.klarna.mobile.sdk.klarna.network.core.api.common.KlarnaSDKError
import com.klarna.mobile.sdk.klarna.network.core.api.klarna.Klarna
import com.klarna.mobile.sdk.klarna.network.messaging.api.KlarnaMessaging
import com.klarna.mobile.sdk.klarna.network.messaging.api.KlarnaMessagingPrequalification
import com.klarna.mobile.sdk.klarna.network.messaging.api.KlarnaMessagingPrequalificationCallback
import com.klarna.mobile.sdk.klarna.network.messaging.api.messaging
import com.klarna.mobile.sdk.klarna.network.messaging.api.models.KlarnaMessagingPrequalificationData
import com.klarna.mobile.sdk.klarna.network.messaging.api.models.KlarnaMessagingPrequalificationResult
import com.klarna.mobile.sdk.reactnative.klarnanetwork.core.KlarnaNetworkCoreModuleImpl
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkConstructor
import io.mockk.mockkStatic
import io.mockk.slot
import io.mockk.unmockkAll
import io.mockk.verify
import java.util.Date
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

class KlarnaNetworkMessagingModuleImplTest {

    private val errorInstanceNotFound = "No instance found for the given instanceId. Call initialize first."
    private val errorActivityNotFound = "Main Activity not found."
    private val errorAmountRequired = "Amount is required"
    private val errorCurrencyRequired = "Currency is required"

    private val mockActivity = mockk<Activity>(relaxed = true)
    private val mockContext = mockk<ReactApplicationContext>(relaxed = true)
    private val mockPromise = mockk<Promise>(relaxed = true)
    private val mockKlarna = mockk<Klarna>(relaxed = true)
    private val mockMessaging = mockk<KlarnaMessaging>(relaxed = true)
    private val mockPrequalification = mockk<KlarnaMessagingPrequalification>(relaxed = true)
    private val writableMap = mockk<WritableMap>(relaxed = true)

    private lateinit var impl: KlarnaNetworkMessagingModuleImpl

    @Before
    fun setUp() {
        mockkStatic("com.klarna.mobile.sdk.klarna.network.messaging.api.KlarnaMessagingKt")
        mockkStatic(Arguments::class)
        mockkConstructor(Handler::class)
        every { Arguments.createMap() } returns writableMap
        every { mockKlarna.messaging } returns mockMessaging
        every { mockMessaging.prequalification } returns mockPrequalification
        every { mockContext.currentActivity } returns mockActivity
        every { anyConstructed<Handler>().post(any()) } answers {
            firstArg<Runnable>().run()
            true
        }
        impl = KlarnaNetworkMessagingModuleImpl(mockContext)
    }

    @After
    fun tearDown() {
        KlarnaNetworkCoreModuleImpl.instances.clear()
        unmockkAll()
    }

    // region Helpers

    private fun givenInstanceRegistered(instanceId: String = "inst_1") {
        KlarnaNetworkCoreModuleImpl.instances[instanceId] = mockKlarna
    }

    private fun givenInitiateReturns(result: KlarnaResult<KlarnaMessagingPrequalificationResult, KlarnaSDKError>) {
        every {
            mockPrequalification.initiate(any(), any(), any())
        } answers {
            thirdArg<KlarnaMessagingPrequalificationCallback>().invoke(result)
        }
    }

    private fun mockError(name: String, message: String): KlarnaSDKError =
        mockk {
            every { this@mockk.name } returns name
            every { this@mockk.message } returns message
        }

    private fun validData(amount: Double = 9999.0, currency: String = "USD"): ReadableMap =
        mockk(relaxed = true) {
            every { hasKey("amount") } returns true
            every { getType("amount") } returns ReadableType.Number
            every { getDouble("amount") } returns amount
            every { hasKey("currency") } returns true
            every { getType("currency") } returns ReadableType.String
            every { getString("currency") } returns currency
        }

    // endregion

    // region initiatePrequalification

    @Test
    fun `initiatePrequalification - rejects when instance not found`() {
        // Given
        val data = validData()

        // When
        impl.initiatePrequalification("unknown_id", data, mockPromise)

        // Then
        verify(exactly = 1) { mockPromise.reject(KlarnaNetworkMessagingModuleImpl.NAME, errorInstanceNotFound) }
        verify(exactly = 0) { mockPrequalification.initiate(any(), any(), any()) }
    }

    @Test
    fun `initiatePrequalification - rejects when activity not found`() {
        // Given
        givenInstanceRegistered()
        every { mockContext.currentActivity } returns null

        // When
        impl.initiatePrequalification("inst_1", validData(), mockPromise)

        // Then
        verify(exactly = 1) { mockPromise.reject(KlarnaNetworkMessagingModuleImpl.NAME, errorActivityNotFound) }
        verify(exactly = 0) { mockPrequalification.initiate(any(), any(), any()) }
    }

    @Test
    fun `initiatePrequalification - rejects with amount required when data is null`() {
        // Given
        givenInstanceRegistered()

        // When
        impl.initiatePrequalification("inst_1", null, mockPromise)

        // Then
        verify(exactly = 1) { mockPromise.reject(KlarnaNetworkMessagingModuleImpl.NAME, errorAmountRequired) }
        verify(exactly = 0) { mockPrequalification.initiate(any(), any(), any()) }
    }

    @Test
    fun `initiatePrequalification - rejects when amount is missing`() {
        // Given
        givenInstanceRegistered()
        val data = mockk<ReadableMap>(relaxed = true) {
            every { hasKey("amount") } returns false
        }

        // When
        impl.initiatePrequalification("inst_1", data, mockPromise)

        // Then
        verify(exactly = 1) { mockPromise.reject(KlarnaNetworkMessagingModuleImpl.NAME, errorAmountRequired) }
        verify(exactly = 0) { mockPrequalification.initiate(any(), any(), any()) }
    }

    @Test
    fun `initiatePrequalification - rejects when currency is missing`() {
        // Given
        givenInstanceRegistered()
        val data = mockk<ReadableMap>(relaxed = true) {
            every { hasKey("amount") } returns true
            every { getType("amount") } returns ReadableType.Number
            every { getDouble("amount") } returns 100.0
            every { hasKey("currency") } returns true
            every { getType("currency") } returns ReadableType.Null
        }

        // When
        impl.initiatePrequalification("inst_1", data, mockPromise)

        // Then
        verify(exactly = 1) { mockPromise.reject(KlarnaNetworkMessagingModuleImpl.NAME, errorCurrencyRequired) }
        verify(exactly = 0) { mockPrequalification.initiate(any(), any(), any()) }
    }

    @Test
    fun `initiatePrequalification - passes activity and data to native sdk`() {
        // Given
        givenInstanceRegistered()
        val dataSlot = slot<KlarnaMessagingPrequalificationData>()
        every { mockPrequalification.initiate(any(), capture(dataSlot), any()) } returns Unit

        // When
        impl.initiatePrequalification("inst_1", validData(amount = 12345.0, currency = "SEK"), mockPromise)

        // Then
        verify(exactly = 1) { mockPrequalification.initiate(mockActivity, any(), any()) }
        assertEquals(12345L, dataSlot.captured.amount)
        assertEquals("SEK", dataSlot.captured.currency)
        verify(exactly = 0) { mockPromise.resolve(any()) }
        verify(exactly = 0) { mockPromise.reject(any<String>(), any<String>()) }
    }

    @Test
    fun `initiatePrequalification - resolves with approved status and ISO expiresAt on success`() {
        // Given
        givenInstanceRegistered()
        givenInitiateReturns(
            KlarnaResult.Success(KlarnaMessagingPrequalificationResult.Approved(Date(1_700_000_000_000L)))
        )

        // When
        impl.initiatePrequalification("inst_1", validData(), mockPromise)

        // Then
        verify(exactly = 1) { writableMap.putString("status", "APPROVED") }
        verify(exactly = 1) { writableMap.putString("expiresAt", "2023-11-14T22:13:20.000Z") }
        verify(exactly = 1) { mockPromise.resolve(writableMap) }
    }

    @Test
    fun `initiatePrequalification - resolves with aborted status on aborted result`() {
        // Given
        givenInstanceRegistered()
        givenInitiateReturns(KlarnaResult.Success(KlarnaMessagingPrequalificationResult.Aborted))

        // When
        impl.initiatePrequalification("inst_1", validData(), mockPromise)

        // Then
        verify(exactly = 1) { writableMap.putString("status", "ABORTED") }
        verify(exactly = 0) { writableMap.putString("expiresAt", any()) }
        verify(exactly = 1) { mockPromise.resolve(writableMap) }
    }

    @Test
    fun `initiatePrequalification - rejects with native error on OPERATION_FAILED failure`() {
        // Given
        givenInstanceRegistered()
        givenInitiateReturns(KlarnaResult.Failure(mockError("OPERATION_FAILED", "Operation failed")))

        // When
        impl.initiatePrequalification("inst_1", validData(), mockPromise)

        // Then
        verify(exactly = 1) { mockPromise.reject("OPERATION_FAILED", "Operation failed") }
        verify(exactly = 0) { mockPromise.resolve(any()) }
    }

    @Test
    fun `initiatePrequalification - rejects with native error on INITIATE_ALREADY_IN_PROGRESS failure`() {
        // Given
        givenInstanceRegistered()
        givenInitiateReturns(
            KlarnaResult.Failure(mockError("INITIATE_ALREADY_IN_PROGRESS", "Initiate already in progress"))
        )

        // When
        impl.initiatePrequalification("inst_1", validData(), mockPromise)

        // Then
        verify(exactly = 1) { mockPromise.reject("INITIATE_ALREADY_IN_PROGRESS", "Initiate already in progress") }
        verify(exactly = 0) { mockPromise.resolve(any()) }
    }

    @Test
    fun `initiatePrequalification - does not throw when promise is null`() {
        // Given
        givenInstanceRegistered()
        givenInitiateReturns(KlarnaResult.Success(KlarnaMessagingPrequalificationResult.Aborted))

        // When
        impl.initiatePrequalification("inst_1", validData(), null)

        // Then
        verify(exactly = 1) { mockPrequalification.initiate(any(), any(), any()) }
    }

    // endregion

    // region dispose

    @Test
    fun `dispose - resolves with null`() {
        // When
        impl.dispose("inst_1", mockPromise)

        // Then
        verify(exactly = 1) { mockPromise.resolve(null) }
    }

    @Test
    fun `dispose - does not affect registered instances`() {
        // Given
        givenInstanceRegistered()

        // When
        impl.dispose("inst_1", mockPromise)

        // Then
        assertEquals(mockKlarna, KlarnaNetworkCoreModuleImpl.instances["inst_1"])
    }

    // endregion
}
