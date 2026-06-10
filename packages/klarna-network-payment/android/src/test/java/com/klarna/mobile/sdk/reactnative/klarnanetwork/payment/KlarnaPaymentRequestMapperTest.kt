package com.klarna.mobile.sdk.reactnative.klarnanetwork.payment

import com.facebook.react.bridge.ReadableArray
import com.facebook.react.bridge.ReadableMap
import com.klarna.mobile.sdk.klarna.network.payment.api.models.KlarnaCollectCustomerProfileType
import com.klarna.mobile.sdk.klarna.network.payment.api.models.KlarnaInterval
import com.klarna.mobile.sdk.klarna.network.payment.api.models.KlarnaRequestCustomerTokenScope
import com.klarna.mobile.sdk.klarna.network.payment.api.models.KlarnaShippingConfigMode
import com.klarna.mobile.sdk.klarna.network.payment.api.models.KlarnaShippingType
import com.klarna.mobile.sdk.klarna.network.payment.api.presentation.models.KlarnaPaymentPresentationIntent
import io.mockk.every
import io.mockk.mockk
import io.mockk.unmockkAll
import org.junit.After
import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertNull

class KlarnaPaymentRequestMapperTest {

    @After
    fun tearDown() {
        unmockkAll()
    }

    // region Helpers

    private fun givenMinimalPaymentRequestMap(
        amount: Double = 1000.0,
        currency: String? = "SEK",
    ): ReadableMap = mockk {
        every { hasKey("amount") } returns true
        every { getDouble("amount") } returns amount
        every { getString("currency") } returns currency
        every { getString("paymentOptionId") } returns null
        every { getString("paymentRequestReference") } returns null
        every { getMap("requestCustomerToken") } returns null
        every { getMap("shippingConfig") } returns null
        every { getArray("collectCustomerProfile") } returns null
        every { getMap("supplementaryPurchaseData") } returns null
    }

    private fun givenPresentationDataMap(
        amount: Double = 500.0,
        currency: String = "USD",
        intent: String? = null,
        paymentProgramEnablementCodes: ReadableArray? = null,
        subscriptionBillingInterval: String? = null,
        hasBillingIntervalFrequency: Boolean = false,
        billingIntervalFrequencyIsNull: Boolean = false,
        billingIntervalFrequency: Int = 0,
    ): ReadableMap = mockk {
        every { getDouble("amount") } returns amount
        every { getString("currency") } returns currency
        every { getString("intent") } returns intent
        every { getArray("paymentProgramEnablementCodes") } returns paymentProgramEnablementCodes
        every { getString("subscriptionBillingInterval") } returns subscriptionBillingInterval
        every { hasKey("subscriptionBillingIntervalFrequency") } returns hasBillingIntervalFrequency
        every { isNull("subscriptionBillingIntervalFrequency") } returns billingIntervalFrequencyIsNull
        every { getInt("subscriptionBillingIntervalFrequency") } returns billingIntervalFrequency
    }

    // endregion

    // region buildPaymentRequestData tests

    @Test
    fun `buildPaymentRequestData - throws when amount key is missing`() {
        val map = mockk<ReadableMap> {
            every { hasKey("amount") } returns false
        }

        assertFailsWith<IllegalArgumentException> {
            KlarnaPaymentRequestMapper.buildPaymentRequestData(map)
        }
    }

    @Test
    fun `buildPaymentRequestData - throws when currency is null`() {
        val map = givenMinimalPaymentRequestMap(currency = null)

        assertFailsWith<IllegalArgumentException> {
            KlarnaPaymentRequestMapper.buildPaymentRequestData(map)
        }
    }

    @Test
    fun `buildPaymentRequestData - throws when currency is empty`() {
        val map = givenMinimalPaymentRequestMap(currency = "")

        assertFailsWith<IllegalArgumentException> {
            KlarnaPaymentRequestMapper.buildPaymentRequestData(map)
        }
    }

    @Test
    fun `buildPaymentRequestData - maps amount and currency correctly`() {
        val map = givenMinimalPaymentRequestMap(amount = 2500.0, currency = "EUR")

        val result = KlarnaPaymentRequestMapper.buildPaymentRequestData(map)

        assertEquals(2500L, result.amount)
        assertEquals("EUR", result.currency)
    }

    @Test
    fun `buildPaymentRequestData - maps optional top-level string fields`() {
        val map = mockk<ReadableMap> {
            every { hasKey("amount") } returns true
            every { getDouble("amount") } returns 100.0
            every { getString("currency") } returns "SEK"
            every { getString("paymentOptionId") } returns "pay_opt_123"
            every { getString("paymentRequestReference") } returns "ref_abc"
            every { getMap("requestCustomerToken") } returns null
            every { getMap("shippingConfig") } returns null
            every { getArray("collectCustomerProfile") } returns null
            every { getMap("supplementaryPurchaseData") } returns null
        }

        val result = KlarnaPaymentRequestMapper.buildPaymentRequestData(map)

        assertEquals("pay_opt_123", result.paymentOptionId)
        assertEquals("ref_abc", result.paymentRequestReference)
    }

    @Test
    fun `buildPaymentRequestData - passes null for optional maps and arrays when absent`() {
        val map = givenMinimalPaymentRequestMap()

        val result = KlarnaPaymentRequestMapper.buildPaymentRequestData(map)

        assertNull(result.requestCustomerToken)
        assertNull(result.shippingConfig)
        assertNull(result.collectCustomerProfile)
        assertNull(result.supplementaryPurchaseData)
    }

    @Test
    fun `buildPaymentRequestData - maps requestCustomerToken scopes correctly`() {
        val scopesArray = mockk<ReadableArray> {
            every { size() } returns 2
            every { getString(0) } returns KlarnaRequestCustomerTokenScope.CUSTOMER_LOGIN.toString()
            every { getString(1) } returns KlarnaRequestCustomerTokenScope.PAYMENT_CUSTOMER_PRESENT.toString()
        }
        val tokenMap = mockk<ReadableMap> {
            every { getArray("scopes") } returns scopesArray
            every { getString("customerTokenReference") } returns "tok_ref_1"
        }
        val map = mockk<ReadableMap> {
            every { hasKey("amount") } returns true
            every { getDouble("amount") } returns 100.0
            every { getString("currency") } returns "SEK"
            every { getString("paymentOptionId") } returns null
            every { getString("paymentRequestReference") } returns null
            every { getMap("requestCustomerToken") } returns tokenMap
            every { getMap("shippingConfig") } returns null
            every { getArray("collectCustomerProfile") } returns null
            every { getMap("supplementaryPurchaseData") } returns null
        }

        val result = KlarnaPaymentRequestMapper.buildPaymentRequestData(map)

        val token = result.requestCustomerToken
        assertEquals(2, token?.scopes?.size)
        assertEquals(KlarnaRequestCustomerTokenScope.CUSTOMER_LOGIN, token?.scopes?.get(0))
        assertEquals(KlarnaRequestCustomerTokenScope.PAYMENT_CUSTOMER_PRESENT, token?.scopes?.get(1))
        assertEquals("tok_ref_1", token?.customerTokenReference)
    }

    @Test
    fun `buildPaymentRequestData - maps shippingConfig mode correctly`() {
        val shippingConfigMap = mockk<ReadableMap> {
            every { getString("mode") } returns KlarnaShippingConfigMode.EDITABLE.toString()
            every { getArray("supportedCountries") } returns null
        }
        val map = mockk<ReadableMap> {
            every { hasKey("amount") } returns true
            every { getDouble("amount") } returns 100.0
            every { getString("currency") } returns "SEK"
            every { getString("paymentOptionId") } returns null
            every { getString("paymentRequestReference") } returns null
            every { getMap("requestCustomerToken") } returns null
            every { getMap("shippingConfig") } returns shippingConfigMap
            every { getArray("collectCustomerProfile") } returns null
            every { getMap("supplementaryPurchaseData") } returns null
        }

        val result = KlarnaPaymentRequestMapper.buildPaymentRequestData(map)

        assertEquals(KlarnaShippingConfigMode.EDITABLE, result.shippingConfig?.mode)
        assertNull(result.shippingConfig?.supportedCountries)
    }

    @Test
    fun `buildPaymentRequestData - defaults shippingConfig mode to EDITABLE for unknown string`() {
        val shippingConfigMap = mockk<ReadableMap> {
            every { getString("mode") } returns "UNKNOWN_MODE"
            every { getArray("supportedCountries") } returns null
        }
        val map = mockk<ReadableMap> {
            every { hasKey("amount") } returns true
            every { getDouble("amount") } returns 100.0
            every { getString("currency") } returns "SEK"
            every { getString("paymentOptionId") } returns null
            every { getString("paymentRequestReference") } returns null
            every { getMap("requestCustomerToken") } returns null
            every { getMap("shippingConfig") } returns shippingConfigMap
            every { getArray("collectCustomerProfile") } returns null
            every { getMap("supplementaryPurchaseData") } returns null
        }

        val result = KlarnaPaymentRequestMapper.buildPaymentRequestData(map)

        assertEquals(KlarnaShippingConfigMode.EDITABLE, result.shippingConfig?.mode)
    }

    @Test
    fun `buildPaymentRequestData - maps collectCustomerProfile array`() {
        val profileArray = mockk<ReadableArray> {
            every { size() } returns 2
            every { getString(0) } returns KlarnaCollectCustomerProfileType.EMAIL.toString()
            every { getString(1) } returns KlarnaCollectCustomerProfileType.PHONE.toString()
        }
        val map = mockk<ReadableMap> {
            every { hasKey("amount") } returns true
            every { getDouble("amount") } returns 100.0
            every { getString("currency") } returns "SEK"
            every { getString("paymentOptionId") } returns null
            every { getString("paymentRequestReference") } returns null
            every { getMap("requestCustomerToken") } returns null
            every { getMap("shippingConfig") } returns null
            every { getArray("collectCustomerProfile") } returns profileArray
            every { getMap("supplementaryPurchaseData") } returns null
        }

        val result = KlarnaPaymentRequestMapper.buildPaymentRequestData(map)

        val profiles = result.collectCustomerProfile
        assertEquals(2, profiles?.size)
        assertEquals(KlarnaCollectCustomerProfileType.EMAIL, profiles?.get(0))
        assertEquals(KlarnaCollectCustomerProfileType.PHONE, profiles?.get(1))
    }

    // endregion

    // region buildPresentationData tests

    @Test
    fun `buildPresentationData - maps amount and currency`() {
        val map = givenPresentationDataMap(amount = 750.0, currency = "GBP")

        val result = KlarnaPaymentRequestMapper.buildPresentationData(map)

        assertEquals(750L, result.amount)
        assertEquals("GBP", result.currency)
    }

    @Test
    fun `buildPresentationData - maps known intent string to enum`() {
        val map = givenPresentationDataMap(intent = KlarnaPaymentPresentationIntent.PAY.toString())

        val result = KlarnaPaymentRequestMapper.buildPresentationData(map)

        assertEquals(KlarnaPaymentPresentationIntent.PAY, result.intent)
    }

    @Test
    fun `buildPresentationData - maps unknown intent string to null`() {
        val map = givenPresentationDataMap(intent = "COMPLETELY_UNKNOWN")

        val result = KlarnaPaymentRequestMapper.buildPresentationData(map)

        assertNull(result.intent)
    }

    @Test
    fun `buildPresentationData - maps null intent to null`() {
        val map = givenPresentationDataMap(intent = null)

        val result = KlarnaPaymentRequestMapper.buildPresentationData(map)

        assertNull(result.intent)
    }

    @Test
    fun `buildPresentationData - maps paymentProgramEnablementCodes array`() {
        val codesArray = mockk<ReadableArray> {
            every { size() } returns 2
            every { getString(0) } returns "CODE_A"
            every { getString(1) } returns "CODE_B"
        }
        val map = givenPresentationDataMap(paymentProgramEnablementCodes = codesArray)

        val result = KlarnaPaymentRequestMapper.buildPresentationData(map)

        assertEquals(listOf("CODE_A", "CODE_B"), result.paymentProgramEnablementCodes)
    }

    @Test
    fun `buildPresentationData - maps null paymentProgramEnablementCodes to null`() {
        val map = givenPresentationDataMap(paymentProgramEnablementCodes = null)

        val result = KlarnaPaymentRequestMapper.buildPresentationData(map)

        assertNull(result.paymentProgramEnablementCodes)
    }

    @Test
    fun `buildPresentationData - maps known subscriptionBillingInterval string to enum`() {
        val map = givenPresentationDataMap(subscriptionBillingInterval = KlarnaInterval.MONTH.toString())

        val result = KlarnaPaymentRequestMapper.buildPresentationData(map)

        assertEquals(KlarnaInterval.MONTH, result.subscriptionBillingInterval)
    }

    @Test
    fun `buildPresentationData - maps unknown subscriptionBillingInterval string to null`() {
        val map = givenPresentationDataMap(subscriptionBillingInterval = "DECADE")

        val result = KlarnaPaymentRequestMapper.buildPresentationData(map)

        assertNull(result.subscriptionBillingInterval)
    }

    @Test
    fun `buildPresentationData - maps subscriptionBillingIntervalFrequency when present and non-null`() {
        val map = givenPresentationDataMap(
            hasBillingIntervalFrequency = true,
            billingIntervalFrequencyIsNull = false,
            billingIntervalFrequency = 3,
        )

        val result = KlarnaPaymentRequestMapper.buildPresentationData(map)

        assertEquals(3, result.subscriptionBillingIntervalFrequency)
    }

    @Test
    fun `buildPresentationData - maps subscriptionBillingIntervalFrequency to null when key absent`() {
        val map = givenPresentationDataMap(hasBillingIntervalFrequency = false)

        val result = KlarnaPaymentRequestMapper.buildPresentationData(map)

        assertNull(result.subscriptionBillingIntervalFrequency)
    }

    @Test
    fun `buildPresentationData - maps subscriptionBillingIntervalFrequency to null when value is null`() {
        val map = givenPresentationDataMap(
            hasBillingIntervalFrequency = true,
            billingIntervalFrequencyIsNull = true,
        )

        val result = KlarnaPaymentRequestMapper.buildPresentationData(map)

        assertNull(result.subscriptionBillingIntervalFrequency)
    }

    // endregion

    // region buildShippingConfig (via buildPaymentRequestData) tests

    @Test
    fun `buildPaymentRequestData - maps shippingConfig supportedCountries`() {
        val countriesArray = mockk<ReadableArray> {
            every { size() } returns 2
            every { getString(0) } returns "SE"
            every { getString(1) } returns "NO"
        }
        val shippingConfigMap = mockk<ReadableMap> {
            every { getString("mode") } returns KlarnaShippingConfigMode.EDITABLE.toString()
            every { getArray("supportedCountries") } returns countriesArray
        }
        val map = mockk<ReadableMap> {
            every { hasKey("amount") } returns true
            every { getDouble("amount") } returns 100.0
            every { getString("currency") } returns "SEK"
            every { getString("paymentOptionId") } returns null
            every { getString("paymentRequestReference") } returns null
            every { getMap("requestCustomerToken") } returns null
            every { getMap("shippingConfig") } returns shippingConfigMap
            every { getArray("collectCustomerProfile") } returns null
            every { getMap("supplementaryPurchaseData") } returns null
        }

        val result = KlarnaPaymentRequestMapper.buildPaymentRequestData(map)

        assertEquals(listOf("SE", "NO"), result.shippingConfig?.supportedCountries)
    }

    // endregion

    // region buildShippingOption (via buildSupplementaryPurchaseData) tests

    @Test
    fun `buildPaymentRequestData - maps shippingOption with known type and attributes`() {
        val attrsArray = mockk<ReadableArray> {
            every { size() } returns 1
            every { getString(0) } returns KlarnaShippingType.TO_DOOR.toString()
        }
        val shippingOptionMap = mockk<ReadableMap> {
            every { getString("shippingCarrier") } returns "DHL"
            every { getString("shippingType") } returns KlarnaShippingType.TO_DOOR.toString()
            every { getArray("shippingTypeAttributes") } returns attrsArray
        }
        val shippingMap = mockk<ReadableMap> {
            every { getMap("address") } returns null
            every { getMap("recipient") } returns null
            every { getMap("shippingOption") } returns shippingOptionMap
            every { getString("shippingReference") } returns "ship_ref_1"
        }
        val shippingArray = mockk<ReadableArray> {
            every { size() } returns 1
            every { getMap(0) } returns shippingMap
        }
        val supplementaryMap = mockk<ReadableMap> {
            every { getMap("customer") } returns null
            every { getArray("lineItems") } returns null
            every { getString("purchaseReference") } returns null
            every { getArray("shipping") } returns shippingArray
            every { getMap("ondemandService") } returns null
            every { getArray("subscriptions") } returns null
        }
        val map = mockk<ReadableMap> {
            every { hasKey("amount") } returns true
            every { getDouble("amount") } returns 100.0
            every { getString("currency") } returns "SEK"
            every { getString("paymentOptionId") } returns null
            every { getString("paymentRequestReference") } returns null
            every { getMap("requestCustomerToken") } returns null
            every { getMap("shippingConfig") } returns null
            every { getArray("collectCustomerProfile") } returns null
            every { getMap("supplementaryPurchaseData") } returns supplementaryMap
        }

        val result = KlarnaPaymentRequestMapper.buildPaymentRequestData(map)

        val shippingOption = result.supplementaryPurchaseData?.shipping?.first()?.shippingOption
        assertEquals("DHL", shippingOption?.shippingCarrier)
        assertEquals(KlarnaShippingType.TO_DOOR, shippingOption?.shippingType)
    }

    @Test
    fun `buildPaymentRequestData - defaults shippingOption type to TO_DOOR for unknown string`() {
        val shippingOptionMap = mockk<ReadableMap> {
            every { getString("shippingCarrier") } returns null
            every { getString("shippingType") } returns "TELEPORT"
            every { getArray("shippingTypeAttributes") } returns null
        }
        val shippingMap = mockk<ReadableMap> {
            every { getMap("address") } returns null
            every { getMap("recipient") } returns null
            every { getMap("shippingOption") } returns shippingOptionMap
            every { getString("shippingReference") } returns null
        }
        val shippingArray = mockk<ReadableArray> {
            every { size() } returns 1
            every { getMap(0) } returns shippingMap
        }
        val supplementaryMap = mockk<ReadableMap> {
            every { getMap("customer") } returns null
            every { getArray("lineItems") } returns null
            every { getString("purchaseReference") } returns null
            every { getArray("shipping") } returns shippingArray
            every { getMap("ondemandService") } returns null
            every { getArray("subscriptions") } returns null
        }
        val map = mockk<ReadableMap> {
            every { hasKey("amount") } returns true
            every { getDouble("amount") } returns 100.0
            every { getString("currency") } returns "SEK"
            every { getString("paymentOptionId") } returns null
            every { getString("paymentRequestReference") } returns null
            every { getMap("requestCustomerToken") } returns null
            every { getMap("shippingConfig") } returns null
            every { getArray("collectCustomerProfile") } returns null
            every { getMap("supplementaryPurchaseData") } returns supplementaryMap
        }

        val result = KlarnaPaymentRequestMapper.buildPaymentRequestData(map)

        val shippingOption = result.supplementaryPurchaseData?.shipping?.first()?.shippingOption
        assertEquals(KlarnaShippingType.TO_DOOR, shippingOption?.shippingType)
    }

    // endregion
}
