package com.klarna.mobile.sdk.reactnative.klarnanetwork.messaging

import com.facebook.react.bridge.Arguments
import com.facebook.react.bridge.ReadableMap
import com.facebook.react.bridge.ReadableType
import com.facebook.react.bridge.WritableMap
import com.klarna.mobile.sdk.klarna.network.messaging.api.models.KlarnaMessagingPrequalificationData
import com.klarna.mobile.sdk.klarna.network.messaging.api.models.KlarnaMessagingPrequalificationResult
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.TimeZone
import kotlin.math.floor

internal object KlarnaMessagingPrequalificationMapper {

    private const val AMOUNT = "amount"
    private const val CURRENCY = "currency"
    private const val STATUS = "status"
    private const val EXPIRES_AT = "expiresAt"
    private const val STATUS_APPROVED = "APPROVED"
    private const val STATUS_ABORTED = "ABORTED"
    private const val ISO_8601_UTC_PATTERN = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'"
    private const val UTC_TIME_ZONE_ID = "UTC"
    private const val ERROR_AMOUNT_REQUIRED = "Amount is required"
    private const val ERROR_CURRENCY_REQUIRED = "Currency is required"

    /**
     * Builds the native prequalification data from the JS payload, mirroring the iOS parsing.
     *
     * `amount` must be a finite, whole JS number representable as a [Long]. `currency` must be a
     * non-empty string. `amount` is validated first and a null [data] is treated as an empty map.
     * Positivity and trimming are enforced by the TS layer and the native SDK, not here.
     *
     * @throws IllegalArgumentException when amount or currency are missing or invalid.
     */
    fun buildPrequalificationData(data: ReadableMap?): KlarnaMessagingPrequalificationData {
        val amount = requireNotNull(data?.parseAmount()) { ERROR_AMOUNT_REQUIRED }
        val currency = requireNotNull(data?.parseCurrency()) { ERROR_CURRENCY_REQUIRED }
        return KlarnaMessagingPrequalificationData(amount = amount, currency = currency)
    }

    fun toWritableMap(result: KlarnaMessagingPrequalificationResult): WritableMap =
        Arguments.createMap().apply {
            when (result) {
                is KlarnaMessagingPrequalificationResult.Approved -> {
                    putString(STATUS, STATUS_APPROVED)
                    putString(EXPIRES_AT, formatIso8601Utc(result.expiresAt))
                }
                is KlarnaMessagingPrequalificationResult.Aborted -> putString(STATUS, STATUS_ABORTED)
            }
        }

    private fun ReadableMap.parseAmount(): Long? {
        if (!hasKey(AMOUNT) || getType(AMOUNT) != ReadableType.Number) return null
        val amount = getDouble(AMOUNT)
        val isWholeNumber = amount.isFinite() && amount == floor(amount)
        // Long.MAX_VALUE.toDouble() is 2^63, which is itself out of range, hence the exclusive upper bound.
        val isInLongRange = amount >= Long.MIN_VALUE.toDouble() && amount < Long.MAX_VALUE.toDouble()
        return amount.toLong().takeIf { isWholeNumber && isInLongRange }
    }

    private fun ReadableMap.parseCurrency(): String? {
        if (!hasKey(CURRENCY) || getType(CURRENCY) != ReadableType.String) return null
        return getString(CURRENCY)?.takeIf { it.isNotEmpty() }
    }

    // SimpleDateFormat is not thread-safe, so a fresh instance is created per call.
    private fun formatIso8601Utc(date: Date): String =
        SimpleDateFormat(ISO_8601_UTC_PATTERN, Locale.US).apply {
            timeZone = TimeZone.getTimeZone(UTC_TIME_ZONE_ID)
        }.format(date)
}
