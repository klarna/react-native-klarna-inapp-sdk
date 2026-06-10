package com.klarna.mobile.sdk.reactnative.klarnanetwork.payment

import com.facebook.react.bridge.Arguments
import com.facebook.react.bridge.WritableArray
import com.facebook.react.bridge.WritableMap
import com.klarna.mobile.sdk.klarna.network.core.api.models.KlarnaAddress
import com.klarna.mobile.sdk.klarna.network.core.api.models.KlarnaCustomerProfile
import com.klarna.mobile.sdk.klarna.network.payment.api.KlarnaPaymentRequest
import com.klarna.mobile.sdk.klarna.network.payment.api.models.KlarnaCustomer
import com.klarna.mobile.sdk.klarna.network.payment.api.models.KlarnaPaymentRequestState
import com.klarna.mobile.sdk.klarna.network.payment.api.models.KlarnaPaymentRequestStateContext
import com.klarna.mobile.sdk.klarna.network.payment.api.models.KlarnaShipping
import com.klarna.mobile.sdk.klarna.network.payment.api.models.KlarnaShippingOption
import com.klarna.mobile.sdk.klarna.network.payment.api.models.KlarnaShippingRecipient
import com.klarna.mobile.sdk.klarna.network.payment.api.models.KlarnaShippingType
import com.klarna.mobile.sdk.klarna.network.payment.api.models.KlarnaShippingTypeAttribute
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkStatic
import io.mockk.unmockkAll
import io.mockk.verify
import org.junit.After
import org.junit.Before
import org.junit.Test

class KlarnaPaymentResponseMapperTest {

    private lateinit var writableMap: WritableMap
    private lateinit var writableArray: WritableArray

    @Before
    fun setUp() {
        mockkStatic(Arguments::class)
        writableMap = mockk(relaxed = true)
        writableArray = mockk(relaxed = true)
        every { Arguments.createMap() } returns writableMap
        every { Arguments.createArray() } returns writableArray
    }

    @After
    fun tearDown() {
        unmockkAll()
    }

    // region Helpers

    /**
     * Stubs Arguments.createMap() to return [maps] in order: first call returns maps[0],
     * second returns maps[1], etc. After all maps are consumed the last one is reused.
     * Use this when the SUT calls createMap() multiple times in a single toWritableMap() call.
     */
    private fun givenCreateMapReturnsInOrder(vararg maps: WritableMap) {
        every { Arguments.createMap() } returnsMany maps.toList()
    }

    // endregion

    // region KlarnaPaymentRequest.toWritableMap tests

    @Test
    fun `KlarnaPaymentRequest toWritableMap - puts paymentRequestId and state`() {
        // Given
        val request = mockk<KlarnaPaymentRequest> {
            every { paymentRequestId } returns "pr_abc"
            every { state } returns KlarnaPaymentRequestState.SUBMITTED
            every { previousState } returns null
            every { stateReason } returns null
            every { paymentRequestReference } returns "ref_123"
            every { stateContext } returns null
        }

        // When
        request.toWritableMap()

        // Then
        verify(exactly = 1) { writableMap.putString("paymentRequestId", "pr_abc") }
        verify(exactly = 1) { writableMap.putString("state", KlarnaPaymentRequestState.SUBMITTED.toString()) }
        verify(exactly = 1) { writableMap.putString("paymentRequestReference", "ref_123") }
    }

    @Test
    fun `KlarnaPaymentRequest toWritableMap - puts null stateContext when absent`() {
        // Given
        val request = mockk<KlarnaPaymentRequest> {
            every { paymentRequestId } returns "pr_abc"
            every { state } returns KlarnaPaymentRequestState.SUBMITTED
            every { previousState } returns null
            every { stateReason } returns null
            every { paymentRequestReference } returns null
            every { stateContext } returns null
        }

        // When
        request.toWritableMap()

        // Then
        verify(exactly = 1) { writableMap.putNull("stateContext") }
        verify(exactly = 0) { writableMap.putMap(eq("stateContext"), any()) }
    }

    @Test
    fun `KlarnaPaymentRequest toWritableMap - puts stateContext map when present`() {
        // Given
        val outerMap = mockk<WritableMap>(relaxed = true)
        val contextMap = mockk<WritableMap>(relaxed = true)
        givenCreateMapReturnsInOrder(outerMap, contextMap)

        val mockStateContext = mockk<KlarnaPaymentRequestStateContext> {
            every { klarnaNetworkSessionToken } returns "session_tok"
            every { klarnaCustomer } returns null
            every { shipping } returns null
        }
        val request = mockk<KlarnaPaymentRequest> {
            every { paymentRequestId } returns "pr_abc"
            every { state } returns KlarnaPaymentRequestState.IN_PROGRESS
            every { previousState } returns null
            every { stateReason } returns null
            every { paymentRequestReference } returns null
            every { stateContext } returns mockStateContext
        }

        // When
        request.toWritableMap()

        // Then
        verify(exactly = 1) { outerMap.putMap("stateContext", contextMap) }
    }

    // endregion

    // region KlarnaCustomerProfile.toWritableMap tests

    @Test
    fun `KlarnaCustomerProfile toWritableMap - puts emailVerified boolean when non-null`() {
        // Given
        val profile = mockk<KlarnaCustomerProfile> {
            every { address } returns null
            every { customerId } returns "cust_1"
            every { country } returns "SE"
            every { email } returns "user@example.com"
            every { emailVerified } returns true
            every { familyName } returns "Doe"
            every { givenName } returns "Jane"
            every { locale } returns "en-SE"
            every { phone } returns null
            every { phoneVerified } returns null
        }

        // When
        profile.toWritableMap()

        // Then
        verify(exactly = 1) { writableMap.putBoolean("emailVerified", true) }
        verify(exactly = 0) { writableMap.putNull("emailVerified") }
    }

    @Test
    fun `KlarnaCustomerProfile toWritableMap - puts null emailVerified when absent`() {
        // Given
        val profile = mockk<KlarnaCustomerProfile> {
            every { address } returns null
            every { customerId } returns null
            every { country } returns null
            every { email } returns null
            every { emailVerified } returns null
            every { familyName } returns null
            every { givenName } returns null
            every { locale } returns null
            every { phone } returns null
            every { phoneVerified } returns null
        }

        // When
        profile.toWritableMap()

        // Then
        verify(exactly = 1) { writableMap.putNull("emailVerified") }
        verify(exactly = 0) { writableMap.putBoolean(eq("emailVerified"), any()) }
    }

    @Test
    fun `KlarnaCustomerProfile toWritableMap - puts phoneVerified boolean when non-null`() {
        // Given
        val profile = mockk<KlarnaCustomerProfile> {
            every { address } returns null
            every { customerId } returns null
            every { country } returns null
            every { email } returns null
            every { emailVerified } returns null
            every { familyName } returns null
            every { givenName } returns null
            every { locale } returns null
            every { phone } returns null
            every { phoneVerified } returns false
        }

        // When
        profile.toWritableMap()

        // Then
        verify(exactly = 1) { writableMap.putBoolean("phoneVerified", false) }
        verify(exactly = 0) { writableMap.putNull("phoneVerified") }
    }

    @Test
    fun `KlarnaCustomerProfile toWritableMap - puts address map when present`() {
        // Given
        val profileMap = mockk<WritableMap>(relaxed = true)
        val addressMap = mockk<WritableMap>(relaxed = true)
        givenCreateMapReturnsInOrder(profileMap, addressMap)

        val address = mockk<KlarnaAddress> {
            every { city } returns "Stockholm"
            every { country } returns "SE"
            every { postalCode } returns "11122"
            every { region } returns null
            every { streetAddress } returns "Sveavägen 1"
            every { streetAddress2 } returns null
        }
        val profile = mockk<KlarnaCustomerProfile> {
            every { this@mockk.address } returns address
            every { customerId } returns null
            every { country } returns null
            every { email } returns null
            every { emailVerified } returns null
            every { familyName } returns null
            every { givenName } returns null
            every { locale } returns null
            every { phone } returns null
            every { phoneVerified } returns null
        }

        // When
        profile.toWritableMap()

        // Then
        verify(exactly = 1) { profileMap.putMap("address", addressMap) }
    }

    // endregion

    // region KlarnaShippingOption.toWritableMap tests

    @Test
    fun `KlarnaShippingOption toWritableMap - puts shippingTypeAttributes array when present`() {
        // Given
        val option = mockk<KlarnaShippingOption> {
            every { shippingCarrier } returns "PostNord"
            every { shippingType } returns KlarnaShippingType.TO_DOOR
            every { shippingTypeAttributes } returns listOf(KlarnaShippingTypeAttribute.SIGNATURE_REQUIRED)
        }

        // When
        option.toWritableMap()

        // Then
        verify(exactly = 1) { writableArray.pushString(KlarnaShippingTypeAttribute.SIGNATURE_REQUIRED.toString()) }
        verify(exactly = 1) { writableMap.putArray("shippingTypeAttributes", writableArray) }
        verify(exactly = 0) { writableMap.putNull("shippingTypeAttributes") }
    }

    @Test
    fun `KlarnaShippingOption toWritableMap - puts null shippingTypeAttributes when absent`() {
        // Given
        val option = mockk<KlarnaShippingOption> {
            every { shippingCarrier } returns null
            every { shippingType } returns KlarnaShippingType.TO_MAILBOX
            every { shippingTypeAttributes } returns null
        }

        // When
        option.toWritableMap()

        // Then
        verify(exactly = 1) { writableMap.putNull("shippingTypeAttributes") }
        verify(exactly = 0) { writableMap.putArray(eq("shippingTypeAttributes"), any()) }
    }

    @Test
    fun `KlarnaShippingOption toWritableMap - puts shippingCarrier and shippingType`() {
        // Given
        val option = mockk<KlarnaShippingOption> {
            every { shippingCarrier } returns "DHL"
            every { shippingType } returns KlarnaShippingType.PICKUP_BOX
            every { shippingTypeAttributes } returns null
        }

        // When
        option.toWritableMap()

        // Then
        verify(exactly = 1) { writableMap.putString("shippingCarrier", "DHL") }
        verify(exactly = 1) { writableMap.putString("shippingType", KlarnaShippingType.PICKUP_BOX.toString()) }
    }

    // endregion

    // region KlarnaShipping.toWritableMap tests

    @Test
    fun `KlarnaShipping toWritableMap - puts address map when present`() {
        // Given
        val shippingMap = mockk<WritableMap>(relaxed = true)
        val addressMap = mockk<WritableMap>(relaxed = true)
        givenCreateMapReturnsInOrder(shippingMap, addressMap)

        val address = mockk<KlarnaAddress> {
            every { city } returns "Gothenburg"
            every { country } returns "SE"
            every { postalCode } returns "41101"
            every { region } returns null
            every { streetAddress } returns "Avenyn 1"
            every { streetAddress2 } returns null
        }
        val shipping = mockk<KlarnaShipping> {
            every { this@mockk.address } returns address
            every { recipient } returns null
            every { shippingOption } returns null
            every { shippingReference } returns null
        }

        // When
        shipping.toWritableMap()

        // Then
        verify(exactly = 1) { shippingMap.putMap("address", addressMap) }
    }

    @Test
    fun `KlarnaShipping toWritableMap - puts null address when absent`() {
        // Given
        val shipping = mockk<KlarnaShipping> {
            every { address } returns null
            every { recipient } returns null
            every { shippingOption } returns null
            every { shippingReference } returns "ref_ship_1"
        }

        // When
        shipping.toWritableMap()

        // Then
        verify(exactly = 1) { writableMap.putNull("address") }
        verify(exactly = 1) { writableMap.putString("shippingReference", "ref_ship_1") }
    }

    // endregion

    // region KlarnaAddress.toWritableMap tests

    @Test
    fun `KlarnaAddress toWritableMap - puts all address fields`() {
        // Given
        val address = mockk<KlarnaAddress> {
            every { city } returns "Malmö"
            every { country } returns "SE"
            every { postalCode } returns "21120"
            every { region } returns "Skåne"
            every { streetAddress } returns "Stortorget 1"
            every { streetAddress2 } returns "Floor 2"
        }

        // When
        address.toWritableMap()

        // Then
        verify(exactly = 1) { writableMap.putString("city", "Malmö") }
        verify(exactly = 1) { writableMap.putString("country", "SE") }
        verify(exactly = 1) { writableMap.putString("postalCode", "21120") }
        verify(exactly = 1) { writableMap.putString("region", "Skåne") }
        verify(exactly = 1) { writableMap.putString("streetAddress", "Stortorget 1") }
        verify(exactly = 1) { writableMap.putString("streetAddress2", "Floor 2") }
    }

    // endregion

    // region KlarnaShippingRecipient.toWritableMap tests

    @Test
    fun `KlarnaShippingRecipient toWritableMap - puts all recipient fields`() {
        // Given
        val recipient = mockk<KlarnaShippingRecipient> {
            every { attention } returns "Attn: Reception"
            every { email } returns "recipient@example.com"
            every { familyName } returns "Smith"
            every { givenName } returns "John"
            every { phone } returns "+46700000000"
        }

        // When
        recipient.toWritableMap()

        // Then
        verify(exactly = 1) { writableMap.putString("attention", "Attn: Reception") }
        verify(exactly = 1) { writableMap.putString("email", "recipient@example.com") }
        verify(exactly = 1) { writableMap.putString("familyName", "Smith") }
        verify(exactly = 1) { writableMap.putString("givenName", "John") }
        verify(exactly = 1) { writableMap.putString("phone", "+46700000000") }
    }

    // endregion

    // region KlarnaCustomer.toWritableMap tests

    @Test
    fun `KlarnaCustomer toWritableMap - puts customerToken and customerTokenReference`() {
        // Given
        val customer = mockk<KlarnaCustomer> {
            every { customerToken } returns "tok_xyz"
            every { customerTokenReference } returns "tok_ref_xyz"
            every { customerProfile } returns null
        }

        // When
        customer.toWritableMap()

        // Then
        verify(exactly = 1) { writableMap.putString("customerToken", "tok_xyz") }
        verify(exactly = 1) { writableMap.putString("customerTokenReference", "tok_ref_xyz") }
        verify(exactly = 1) { writableMap.putNull("customerProfile") }
    }

    @Test
    fun `KlarnaCustomer toWritableMap - puts customerProfile map when present`() {
        // Given
        val customerMap = mockk<WritableMap>(relaxed = true)
        val profileMap = mockk<WritableMap>(relaxed = true)
        givenCreateMapReturnsInOrder(customerMap, profileMap)

        val profile = mockk<KlarnaCustomerProfile> {
            every { address } returns null
            every { customerId } returns null
            every { country } returns null
            every { email } returns null
            every { emailVerified } returns null
            every { familyName } returns null
            every { givenName } returns null
            every { locale } returns null
            every { phone } returns null
            every { phoneVerified } returns null
        }
        val customer = mockk<KlarnaCustomer> {
            every { customerToken } returns null
            every { customerTokenReference } returns null
            every { customerProfile } returns profile
        }

        // When
        customer.toWritableMap()

        // Then
        verify(exactly = 1) { customerMap.putMap("customerProfile", profileMap) }
    }

    // endregion
}
