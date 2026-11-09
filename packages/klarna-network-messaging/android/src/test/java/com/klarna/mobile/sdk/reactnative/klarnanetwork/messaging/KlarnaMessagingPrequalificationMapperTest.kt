package com.klarna.mobile.sdk.reactnative.klarnanetwork.messaging

import com.facebook.react.bridge.Arguments
import com.facebook.react.bridge.ReadableMap
import com.facebook.react.bridge.ReadableType
import com.facebook.react.bridge.WritableMap
import com.klarna.mobile.sdk.klarna.network.messaging.api.models.KlarnaMessagingPrequalificationResult
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkStatic
import io.mockk.unmockkAll
import io.mockk.verify
import java.util.Date
import java.util.Locale
import java.util.TimeZone
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertThrows
import org.junit.Before
import org.junit.Test

class KlarnaMessagingPrequalificationMapperTest {

    private val amountKey = "amount"
    private val currencyKey = "currency"
    private val errorAmountRequired = "Amount is required"
    private val errorCurrencyRequired = "Currency is required"

    private lateinit var writableMap: WritableMap

    @Before
    fun setUp() {
        writableMap = mockk(relaxed = true)
        mockkStatic(Arguments::class)
        every { Arguments.createMap() } returns writableMap
    }

    @After
    fun tearDown() {
        unmockkAll()
    }

    // region Helpers

    private fun ReadableMap.givenAmount(amount: Double, type: ReadableType = ReadableType.Number) {
        every { hasKey(amountKey) } returns true
        every { getType(amountKey) } returns type
        every { getDouble(amountKey) } returns amount
    }

    private fun ReadableMap.givenCurrency(currency: String, type: ReadableType = ReadableType.String) {
        every { hasKey(currencyKey) } returns true
        every { getType(currencyKey) } returns type
        every { getString(currencyKey) } returns currency
    }

    private fun givenData(
        amount: Double = 100.0,
        amountType: ReadableType = ReadableType.Number,
        currency: String = "USD",
        currencyType: ReadableType = ReadableType.String,
    ): ReadableMap = mockk(relaxed = true) {
        givenAmount(amount, amountType)
        givenCurrency(currency, currencyType)
    }

    // endregion

    // region buildPrequalificationData

    @Test
    fun `buildPrequalificationData - maps amount and currency`() {
        // Given
        val data = givenData(amount = 9999.0, currency = "USD")

        // When
        val result = KlarnaMessagingPrequalificationMapper.buildPrequalificationData(data)

        // Then
        assertEquals(9999L, result.amount)
        assertEquals("USD", result.currency)
    }

    @Test
    fun `buildPrequalificationData - accepts negative whole amount`() {
        // Given
        val data = givenData(amount = -500.0, currency = "USD")

        // When
        val result = KlarnaMessagingPrequalificationMapper.buildPrequalificationData(data)

        // Then
        assertEquals(-500L, result.amount)
    }

    @Test
    fun `buildPrequalificationData - accepts whitespace only currency`() {
        // Given
        val data = givenData(amount = 100.0, currency = "  ")

        // When
        val result = KlarnaMessagingPrequalificationMapper.buildPrequalificationData(data)

        // Then
        assertEquals("  ", result.currency)
    }

    @Test
    fun `buildPrequalificationData - accepts lowest amount within long range`() {
        // Given
        val data = givenData(amount = Long.MIN_VALUE.toDouble(), currency = "USD")

        // When
        val result = KlarnaMessagingPrequalificationMapper.buildPrequalificationData(data)

        // Then
        assertEquals(Long.MIN_VALUE, result.amount)
    }

    @Test
    fun `buildPrequalificationData - throws when data is null`() {
        // When
        val error = assertThrows(IllegalArgumentException::class.java) {
            KlarnaMessagingPrequalificationMapper.buildPrequalificationData(null)
        }

        // Then
        assertEquals(errorAmountRequired, error.message)
    }

    @Test
    fun `buildPrequalificationData - throws when amount key is missing`() {
        // Given
        val data = mockk<ReadableMap>(relaxed = true) {
            every { hasKey(amountKey) } returns false
            givenCurrency("USD")
        }

        // When
        val error = assertThrows(IllegalArgumentException::class.java) {
            KlarnaMessagingPrequalificationMapper.buildPrequalificationData(data)
        }

        // Then
        assertEquals(errorAmountRequired, error.message)
    }

    @Test
    fun `buildPrequalificationData - throws when amount is null`() {
        // Given
        val data = givenData(amountType = ReadableType.Null, currency = "USD")

        // When
        val error = assertThrows(IllegalArgumentException::class.java) {
            KlarnaMessagingPrequalificationMapper.buildPrequalificationData(data)
        }

        // Then
        assertEquals(errorAmountRequired, error.message)
    }

    @Test
    fun `buildPrequalificationData - throws when amount is a string`() {
        // Given
        val data = givenData(amountType = ReadableType.String, currency = "USD")

        // When
        val error = assertThrows(IllegalArgumentException::class.java) {
            KlarnaMessagingPrequalificationMapper.buildPrequalificationData(data)
        }

        // Then
        assertEquals(errorAmountRequired, error.message)
    }

    @Test
    fun `buildPrequalificationData - throws when amount is a boolean`() {
        // Given
        val data = givenData(amountType = ReadableType.Boolean, currency = "USD")

        // When
        val error = assertThrows(IllegalArgumentException::class.java) {
            KlarnaMessagingPrequalificationMapper.buildPrequalificationData(data)
        }

        // Then
        assertEquals(errorAmountRequired, error.message)
    }

    @Test
    fun `buildPrequalificationData - throws when amount has a fractional part`() {
        // Given
        val data = givenData(amount = 99.5, currency = "USD")

        // When
        val error = assertThrows(IllegalArgumentException::class.java) {
            KlarnaMessagingPrequalificationMapper.buildPrequalificationData(data)
        }

        // Then
        assertEquals(errorAmountRequired, error.message)
    }

    @Test
    fun `buildPrequalificationData - throws when amount is NaN`() {
        // Given
        val data = givenData(amount = Double.NaN, currency = "USD")

        // When
        val error = assertThrows(IllegalArgumentException::class.java) {
            KlarnaMessagingPrequalificationMapper.buildPrequalificationData(data)
        }

        // Then
        assertEquals(errorAmountRequired, error.message)
    }

    @Test
    fun `buildPrequalificationData - throws when amount is positive infinity`() {
        // Given
        val data = givenData(amount = Double.POSITIVE_INFINITY, currency = "USD")

        // When
        val error = assertThrows(IllegalArgumentException::class.java) {
            KlarnaMessagingPrequalificationMapper.buildPrequalificationData(data)
        }

        // Then
        assertEquals(errorAmountRequired, error.message)
    }

    @Test
    fun `buildPrequalificationData - throws when amount is negative infinity`() {
        // Given
        val data = givenData(amount = Double.NEGATIVE_INFINITY, currency = "USD")

        // When
        val error = assertThrows(IllegalArgumentException::class.java) {
            KlarnaMessagingPrequalificationMapper.buildPrequalificationData(data)
        }

        // Then
        assertEquals(errorAmountRequired, error.message)
    }

    @Test
    fun `buildPrequalificationData - throws when amount exceeds long range`() {
        // Given
        // Long.MAX_VALUE.toDouble() is 2^63, the first whole number above the Long range.
        val data = givenData(amount = Long.MAX_VALUE.toDouble(), currency = "USD")

        // When
        val error = assertThrows(IllegalArgumentException::class.java) {
            KlarnaMessagingPrequalificationMapper.buildPrequalificationData(data)
        }

        // Then
        assertEquals(errorAmountRequired, error.message)
    }

    @Test
    fun `buildPrequalificationData - throws when amount is below long range`() {
        // Given
        val data = givenData(amount = -1.0e19, currency = "USD")

        // When
        val error = assertThrows(IllegalArgumentException::class.java) {
            KlarnaMessagingPrequalificationMapper.buildPrequalificationData(data)
        }

        // Then
        assertEquals(errorAmountRequired, error.message)
    }

    @Test
    fun `buildPrequalificationData - throws amount required when both amount and currency are missing`() {
        // Given
        val data = mockk<ReadableMap>(relaxed = true) {
            every { hasKey(any()) } returns false
        }

        // When
        val error = assertThrows(IllegalArgumentException::class.java) {
            KlarnaMessagingPrequalificationMapper.buildPrequalificationData(data)
        }

        // Then
        assertEquals(errorAmountRequired, error.message)
    }

    @Test
    fun `buildPrequalificationData - throws when currency key is missing`() {
        // Given
        val data = mockk<ReadableMap>(relaxed = true) {
            givenAmount(100.0)
            every { hasKey(currencyKey) } returns false
        }

        // When
        val error = assertThrows(IllegalArgumentException::class.java) {
            KlarnaMessagingPrequalificationMapper.buildPrequalificationData(data)
        }

        // Then
        assertEquals(errorCurrencyRequired, error.message)
    }

    @Test
    fun `buildPrequalificationData - throws when currency is null`() {
        // Given
        val data = givenData(amount = 100.0, currencyType = ReadableType.Null)

        // When
        val error = assertThrows(IllegalArgumentException::class.java) {
            KlarnaMessagingPrequalificationMapper.buildPrequalificationData(data)
        }

        // Then
        assertEquals(errorCurrencyRequired, error.message)
    }

    @Test
    fun `buildPrequalificationData - throws when currency is empty`() {
        // Given
        val data = givenData(amount = 100.0, currency = "")

        // When
        val error = assertThrows(IllegalArgumentException::class.java) {
            KlarnaMessagingPrequalificationMapper.buildPrequalificationData(data)
        }

        // Then
        assertEquals(errorCurrencyRequired, error.message)
    }

    @Test
    fun `buildPrequalificationData - throws when currency is a number`() {
        // Given
        val data = givenData(amount = 100.0, currencyType = ReadableType.Number)

        // When
        val error = assertThrows(IllegalArgumentException::class.java) {
            KlarnaMessagingPrequalificationMapper.buildPrequalificationData(data)
        }

        // Then
        assertEquals(errorCurrencyRequired, error.message)
    }

    @Test
    fun `buildPrequalificationData - throws when currency is a boolean`() {
        // Given
        val data = givenData(amount = 100.0, currencyType = ReadableType.Boolean)

        // When
        val error = assertThrows(IllegalArgumentException::class.java) {
            KlarnaMessagingPrequalificationMapper.buildPrequalificationData(data)
        }

        // Then
        assertEquals(errorCurrencyRequired, error.message)
    }

    // endregion

    // region toWritableMap

    @Test
    fun `toWritableMap - maps approved result with ISO-8601 UTC expiresAt`() {
        val result = KlarnaMessagingPrequalificationResult.Approved(Date(1_700_000_000_123L))

        val map = KlarnaMessagingPrequalificationMapper.toWritableMap(result)

        assertEquals(writableMap, map)
        verify(exactly = 1) { writableMap.putString("status", "APPROVED") }
        verify(exactly = 1) { writableMap.putString("expiresAt", "2023-11-14T22:13:20.123Z") }
    }

    @Test
    fun `toWritableMap - formats expiresAt in UTC regardless of default time zone`() {
        val originalZone = TimeZone.getDefault()
        val originalLocale = Locale.getDefault()
        try {
            TimeZone.setDefault(TimeZone.getTimeZone("Asia/Tokyo"))
            Locale.setDefault(Locale.forLanguageTag("ar-SA"))
            val result = KlarnaMessagingPrequalificationResult.Approved(Date(0L))

            KlarnaMessagingPrequalificationMapper.toWritableMap(result)

            verify(exactly = 1) { writableMap.putString("expiresAt", "1970-01-01T00:00:00.000Z") }
        } finally {
            TimeZone.setDefault(originalZone)
            Locale.setDefault(originalLocale)
        }
    }

    @Test
    fun `toWritableMap - maps aborted result without expiresAt`() {
        val map = KlarnaMessagingPrequalificationMapper.toWritableMap(
            KlarnaMessagingPrequalificationResult.Aborted
        )

        assertEquals(writableMap, map)
        verify(exactly = 1) { writableMap.putString("status", "ABORTED") }
        verify(exactly = 0) { writableMap.putString("expiresAt", any()) }
    }

    // endregion
}
