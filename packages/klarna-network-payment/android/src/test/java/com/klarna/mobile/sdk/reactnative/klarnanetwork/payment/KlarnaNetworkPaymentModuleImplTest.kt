package com.klarna.mobile.sdk.reactnative.klarnanetwork.payment

import android.app.Activity
import com.facebook.react.bridge.Arguments
import com.facebook.react.bridge.Promise
import com.facebook.react.bridge.ReactApplicationContext
import com.facebook.react.bridge.ReadableMap
import com.klarna.mobile.sdk.klarna.network.core.api.common.KlarnaResult
import com.klarna.mobile.sdk.klarna.network.core.api.common.KlarnaSDKError
import com.klarna.mobile.sdk.klarna.network.core.api.klarna.Klarna
import com.klarna.mobile.sdk.klarna.network.payment.api.KlarnaPayment
import com.klarna.mobile.sdk.klarna.network.payment.api.KlarnaPaymentRequest
import com.klarna.mobile.sdk.klarna.network.payment.api.presentation.KlarnaPaymentPresentation
import com.klarna.mobile.sdk.klarna.network.payment.api.presentation.models.KlarnaPaymentPresentationContent
import com.klarna.mobile.sdk.klarna.network.payment.api.models.KlarnaPaymentRequestData
import com.klarna.mobile.sdk.klarna.network.payment.api.payment
import com.klarna.mobile.sdk.reactnative.klarnanetwork.core.KlarnaNetworkCoreModuleImpl
import android.os.Handler
import android.os.Looper
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkConstructor
import io.mockk.mockkStatic
import io.mockk.unmockkAll
import io.mockk.verify
import org.junit.After
import org.junit.Before
import org.junit.Test

class KlarnaNetworkPaymentModuleImplTest {

    private val errorInstanceNotFound = "No instance found for the given instanceId. Call initialize first."
    private val errorActivityNotFound = "Main Activity not found."
    private val errorDataRequired = "data is required"
    private val errorAmountRequired = "Amount is required"
    private val errorCurrencyRequired = "Currency is required"

    private val mockActivity = mockk<Activity>(relaxed = true)
    private val mockContext = mockk<ReactApplicationContext>(relaxed = true)
    private val mockPromise = mockk<Promise>(relaxed = true)
    private val mockKlarna = mockk<Klarna>(relaxed = true)
    private val mockPayment = mockk<KlarnaPayment>(relaxed = true)
    private val mockPaymentRequest = mockk<KlarnaPaymentRequest>(relaxed = true)
    private val mockPresentation = mockk<KlarnaPaymentPresentation>(relaxed = true)

    private lateinit var impl: KlarnaNetworkPaymentModuleImpl

    @Before
    fun setUp() {
        mockkStatic("com.klarna.mobile.sdk.klarna.network.payment.api.KlarnaPaymentKt")
        mockkStatic(Arguments::class)
        mockkConstructor(Handler::class)
        every { Arguments.createMap() } returns mockk(relaxed = true)
        every { Arguments.createArray() } returns mockk(relaxed = true)
        every { mockKlarna.payment } returns mockPayment
        every { mockPayment.presentation } returns mockPresentation
        every { mockContext.currentActivity } returns mockActivity
        every { anyConstructed<Handler>().post(any()) } answers {
            firstArg<Runnable>().run()
            true
        }
        impl = KlarnaNetworkPaymentModuleImpl(mockContext)
    }

    @After
    fun tearDown() {
        KlarnaNetworkCoreModuleImpl.instances.clear()
        KlarnaNetworkPaymentModuleImpl.storedPresentationContent.clear()
        unmockkAll()
    }

    // region Helpers

    private fun givenInitializeSucceeds(instanceId: String = "inst_1") {
        KlarnaNetworkCoreModuleImpl.instances[instanceId] = mockKlarna
    }

    private fun givenInitiateWithIdSucceeds(instanceId: String = "inst_1") {
        every {
            mockPayment.initiate(activity = any(), paymentRequestId = any(), callback = any())
        } answers {
            thirdArg<(KlarnaResult<KlarnaPaymentRequest, KlarnaSDKError>) -> Unit>()
                .invoke(KlarnaResult.Success(mockPaymentRequest))
        }
        givenInitializeSucceeds(instanceId)
    }

    private fun givenInitiateWithDataSucceeds(instanceId: String = "inst_1") {
        every {
            mockPayment.initiate(activity = any(), paymentRequestData = any(), callback = any())
        } answers {
            thirdArg<(KlarnaResult<KlarnaPaymentRequest, KlarnaSDKError>) -> Unit>()
                .invoke(KlarnaResult.Success(mockPaymentRequest))
        }
        givenInitializeSucceeds(instanceId)
    }

    private fun givenFetchSucceeds(instanceId: String = "inst_1") {
        every {
            mockPayment.fetch(paymentRequestId = any(), callback = any())
        } answers {
            secondArg<(KlarnaResult<KlarnaPaymentRequest, KlarnaSDKError>) -> Unit>()
                .invoke(KlarnaResult.Success(mockPaymentRequest))
        }
        givenInitializeSucceeds(instanceId)
    }

    private fun givenCancelSucceeds(instanceId: String = "inst_1") {
        every {
            mockPayment.cancel(paymentRequestId = any(), callback = any())
        } answers {
            secondArg<(KlarnaResult<KlarnaPaymentRequest, KlarnaSDKError>) -> Unit>()
                .invoke(KlarnaResult.Success(mockPaymentRequest))
        }
        givenInitializeSucceeds(instanceId)
    }

    private fun mockError(name: String = "error_name", message: String = "error_message"): KlarnaSDKError =
        mockk {
            every { this@mockk.name } returns name
            every { this@mockk.message } returns message
        }

    private fun mockValidPaymentData(): ReadableMap = mockk(relaxed = true) {
        every { hasKey("amount") } returns true
        every { getString("currency") } returns "USD"
    }

    // endregion

    // region initiateWithId

    @Test
    fun `initiateWithId - rejects when instance not found`() {
        impl.initiateWithId("unknown_id", "pr_1", mockPromise)
        verify { mockPromise.reject(KlarnaNetworkPaymentModuleImpl.NAME, errorInstanceNotFound) }
    }

    @Test
    fun `initiateWithId - rejects when activity not found`() {
        givenInitializeSucceeds()
        every { mockContext.currentActivity } returns null
        impl.initiateWithId("inst_1", "pr_1", mockPromise)
        verify { mockPromise.reject(KlarnaNetworkPaymentModuleImpl.NAME, errorActivityNotFound) }
    }

    @Test
    fun `initiateWithId - resolves on success`() {
        givenInitiateWithIdSucceeds()
        impl.initiateWithId("inst_1", "pr_1", mockPromise)
        verify { mockPromise.resolve(any()) }
    }

    @Test
    fun `initiateWithId - rejects on native failure`() {
        givenInitializeSucceeds()
        val error = mockError()
        every {
            mockPayment.initiate(activity = any(), paymentRequestId = any(), callback = any())
        } answers {
            thirdArg<(KlarnaResult<KlarnaPaymentRequest, KlarnaSDKError>) -> Unit>()
                .invoke(KlarnaResult.Failure(error))
        }
        impl.initiateWithId("inst_1", "pr_1", mockPromise)
        verify { mockPromise.reject("error_name", "error_message") }
    }

    // endregion

    // region initiateWithData

    @Test
    fun `initiateWithData - rejects when instance not found`() {
        val data = mockk<ReadableMap>(relaxed = true)
        impl.initiateWithData("unknown_id", data, mockPromise)
        verify { mockPromise.reject(KlarnaNetworkPaymentModuleImpl.NAME, errorInstanceNotFound) }
    }

    @Test
    fun `initiateWithData - rejects when activity not found`() {
        givenInitializeSucceeds()
        every { mockContext.currentActivity } returns null
        val data = mockk<ReadableMap>(relaxed = true)
        impl.initiateWithData("inst_1", data, mockPromise)
        verify { mockPromise.reject(KlarnaNetworkPaymentModuleImpl.NAME, errorActivityNotFound) }
    }

    @Test
    fun `initiateWithData - rejects when data is null`() {
        givenInitializeSucceeds()
        impl.initiateWithData("inst_1", null, mockPromise)
        verify { mockPromise.reject(KlarnaNetworkPaymentModuleImpl.NAME, errorDataRequired) }
    }

    @Test
    fun `initiateWithData - rejects when amount is missing`() {
        givenInitializeSucceeds()
        val data = mockk<ReadableMap>(relaxed = true) {
            every { hasKey("amount") } returns false
        }
        impl.initiateWithData("inst_1", data, mockPromise)
        verify { mockPromise.reject(KlarnaNetworkPaymentModuleImpl.NAME, errorAmountRequired) }
    }

    @Test
    fun `initiateWithData - rejects when currency is missing`() {
        givenInitializeSucceeds()
        val data = mockk<ReadableMap>(relaxed = true) {
            every { hasKey("amount") } returns true
            every { getString("currency") } returns null
        }
        impl.initiateWithData("inst_1", data, mockPromise)
        verify { mockPromise.reject(KlarnaNetworkPaymentModuleImpl.NAME, errorCurrencyRequired) }
    }

    @Test
    fun `initiateWithData - resolves on success`() {
        givenInitiateWithDataSucceeds()
        impl.initiateWithData("inst_1", mockValidPaymentData(), mockPromise)
        verify { mockPromise.resolve(any()) }
    }

    @Test
    fun `initiateWithData - rejects on native failure`() {
        givenInitializeSucceeds()
        val error = mockError()
        every {
            mockPayment.initiate(activity = any(), paymentRequestData = any(), callback = any())
        } answers {
            thirdArg<(KlarnaResult<KlarnaPaymentRequest, KlarnaSDKError>) -> Unit>()
                .invoke(KlarnaResult.Failure(error))
        }
        impl.initiateWithData("inst_1", mockValidPaymentData(), mockPromise)
        verify { mockPromise.reject("error_name", "error_message") }
    }

    // endregion

    // region fetch

    @Test
    fun `fetch - rejects when instance not found`() {
        impl.fetch("unknown_id", "pr_1", mockPromise)
        verify { mockPromise.reject(KlarnaNetworkPaymentModuleImpl.NAME, errorInstanceNotFound) }
    }

    @Test
    fun `fetch - resolves on success`() {
        givenFetchSucceeds()
        impl.fetch("inst_1", "pr_1", mockPromise)
        verify { mockPromise.resolve(any()) }
    }

    @Test
    fun `fetch - rejects on native failure`() {
        givenInitializeSucceeds()
        val error = mockError()
        every {
            mockPayment.fetch(paymentRequestId = any(), callback = any())
        } answers {
            secondArg<(KlarnaResult<KlarnaPaymentRequest, KlarnaSDKError>) -> Unit>()
                .invoke(KlarnaResult.Failure(error))
        }
        impl.fetch("inst_1", "pr_1", mockPromise)
        verify { mockPromise.reject("error_name", "error_message") }
    }

    // endregion

    // region cancel

    @Test
    fun `cancel - rejects when instance not found`() {
        impl.cancel("unknown_id", "pr_1", mockPromise)
        verify { mockPromise.reject(KlarnaNetworkPaymentModuleImpl.NAME, errorInstanceNotFound) }
    }

    @Test
    fun `cancel - resolves on success`() {
        givenCancelSucceeds()
        impl.cancel("inst_1", "pr_1", mockPromise)
        verify { mockPromise.resolve(any()) }
    }

    @Test
    fun `cancel - rejects on native failure`() {
        givenInitializeSucceeds()
        val error = mockError()
        every {
            mockPayment.cancel(paymentRequestId = any(), callback = any())
        } answers {
            secondArg<(KlarnaResult<KlarnaPaymentRequest, KlarnaSDKError>) -> Unit>()
                .invoke(KlarnaResult.Failure(error))
        }
        impl.cancel("inst_1", "pr_1", mockPromise)
        verify { mockPromise.reject("error_name", "error_message") }
    }

    // endregion

    // region dispose

    @Test
    fun `dispose - removes stored presentation content and resolves`() {
        KlarnaNetworkPaymentModuleImpl.storedPresentationContent["inst_1"] = mockk(relaxed = true)
        impl.dispose("inst_1", mockPromise)
        assert(!KlarnaNetworkPaymentModuleImpl.storedPresentationContent.containsKey("inst_1"))
        verify { mockPromise.resolve(null) }
    }

    @Test
    fun `dispose - resolves even when no stored content`() {
        impl.dispose("inst_1", mockPromise)
        verify { mockPromise.resolve(null) }
    }

    // endregion

    // region presentationFetch

    @Test
    fun `presentationFetch - rejects when instance not found`() {
        impl.presentationFetch("unknown_id", mockk(relaxed = true), mockPromise)
        verify { mockPromise.reject(KlarnaNetworkPaymentModuleImpl.NAME, errorInstanceNotFound) }
    }

    @Test
    fun `presentationFetch - rejects when data is null`() {
        givenInitializeSucceeds()
        impl.presentationFetch("inst_1", null, mockPromise)
        verify { mockPromise.reject(KlarnaNetworkPaymentModuleImpl.NAME, "data is required") }
    }

    // endregion

    // region presentationHandleLink

    @Test
    fun `presentationHandleLink - rejects when instance not found`() {
        impl.presentationHandleLink("unknown_id", "https://example.com", mockPromise)
        verify { mockPromise.reject(KlarnaNetworkPaymentModuleImpl.NAME, errorInstanceNotFound) }
    }

    @Test
    fun `presentationHandleLink - rejects when no presentation content stored`() {
        givenInitializeSucceeds()
        impl.presentationHandleLink("inst_1", "https://example.com", mockPromise)
        verify { mockPromise.reject(KlarnaNetworkPaymentModuleImpl.NAME, "No presentation content found. Call presentationFetch first.") }
    }

    @Test
    fun `presentationHandleLink - rejects when activity not found`() {
        givenInitializeSucceeds()
        KlarnaNetworkPaymentModuleImpl.storedPresentationContent["inst_1"] = mockk(relaxed = true)
        every { mockContext.currentActivity } returns null
        impl.presentationHandleLink("inst_1", "https://example.com", mockPromise)
        verify { mockPromise.reject(KlarnaNetworkPaymentModuleImpl.NAME, errorActivityNotFound) }
    }

    // endregion
}
