package com.klarna.mobile.sdk.reactnative.klarnanetwork.messaging

import com.klarna.mobile.sdk.api.KlarnaTheme
import com.klarna.mobile.sdk.klarna.network.messaging.api.KlarnaMessagingPlacementConfiguration

internal enum class PlacementType(val value: String) {
    AUTO_SIZE("CreditPromotionAutoSize"),
    BADGE("CreditPromotionBadge");

    companion object {
        fun fromString(value: String?): PlacementType =
            entries.find { it.value == value } ?: AUTO_SIZE
    }
}

internal object KlarnaMessagingViewManagerHelpers {

    fun parseTheme(value: String?): KlarnaTheme? = when (value) {
        "light" -> KlarnaTheme.LIGHT
        "dark" -> KlarnaTheme.DARK
        "automatic" -> KlarnaTheme.AUTOMATIC
        else -> null
    }

    fun parseAmount(value: String?): Long? {
        if (value.isNullOrEmpty()) return null
        return value.toLongOrNull()
    }

    fun resolvePlacementType(value: String?): PlacementType =
        PlacementType.fromString(value)

    fun buildConfiguration(
        placementType: String?,
        theme: KlarnaTheme?,
        amount: Long,
        currency: String
    ): KlarnaMessagingPlacementConfiguration =
        when (resolvePlacementType(placementType)) {
            PlacementType.BADGE ->
                KlarnaMessagingPlacementConfiguration.CreditPromotionBadge(theme, amount, currency)
            PlacementType.AUTO_SIZE ->
                KlarnaMessagingPlacementConfiguration.CreditPromotionAutoSize(theme, amount, currency)
        }

    /**
     * Returns true when an incoming height report should be dispatched
     * (i.e. it differs from the last value already reported).
     */
    fun shouldReportHeight(lastReported: Int?, newHeightDp: Int): Boolean =
        newHeightDp > 0 && lastReported != newHeightDp
}
