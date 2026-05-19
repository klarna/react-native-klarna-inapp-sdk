package com.klarna.mobile.sdk.reactnative.klarnanetwork.messaging

import com.klarna.mobile.sdk.reactnative.klarnanetwork.messaging.KlarnaMessagingViewManagerHelpers.buildConfiguration
import com.klarna.mobile.sdk.reactnative.klarnanetwork.messaging.KlarnaMessagingViewManagerHelpers.parseAmount
import com.klarna.mobile.sdk.reactnative.klarnanetwork.messaging.KlarnaMessagingViewManagerHelpers.parseTheme
import com.klarna.mobile.sdk.reactnative.klarnanetwork.messaging.KlarnaMessagingViewManagerHelpers.resolvePlacementType
import com.klarna.mobile.sdk.reactnative.klarnanetwork.messaging.KlarnaMessagingViewManagerHelpers.shouldReportHeight
import com.klarna.mobile.sdk.api.KlarnaTheme
import com.klarna.mobile.sdk.klarna.network.messaging.api.KlarnaMessagingPlacementConfiguration
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class KlarnaMessagingViewManagerHelpersTest {

    // MARK: - parseTheme

    @Test
    fun parseTheme_light() {
        assertEquals(KlarnaTheme.LIGHT, parseTheme("light"))
    }

    @Test
    fun parseTheme_dark() {
        assertEquals(KlarnaTheme.DARK, parseTheme("dark"))
    }

    @Test
    fun parseTheme_automatic() {
        assertEquals(KlarnaTheme.AUTOMATIC, parseTheme("automatic"))
    }

    @Test
    fun parseTheme_nullInput_returnsNull() {
        assertNull(parseTheme(null))
    }

    @Test
    fun parseTheme_unknownValue_returnsNull() {
        assertNull(parseTheme("neon"))
    }

    @Test
    fun parseTheme_isCaseSensitive() {
        // Contract is lowercase only; uppercase should not match.
        assertNull(parseTheme("LIGHT"))
        assertNull(parseTheme("Light"))
    }

    @Test
    fun parseTheme_emptyString_returnsNull() {
        assertNull(parseTheme(""))
    }

    // MARK: - parseAmount

    @Test
    fun parseAmount_validInteger() {
        assertEquals(12345L, parseAmount("12345"))
    }

    @Test
    fun parseAmount_zero() {
        assertEquals(0L, parseAmount("0"))
    }

    @Test
    fun parseAmount_negative() {
        assertEquals(-500L, parseAmount("-500"))
    }

    @Test
    fun parseAmount_null_returnsNull() {
        assertNull(parseAmount(null))
    }

    @Test
    fun parseAmount_empty_returnsNull() {
        assertNull(parseAmount(""))
    }

    @Test
    fun parseAmount_nonNumeric_returnsNull() {
        assertNull(parseAmount("abc"))
    }

    @Test
    fun parseAmount_fractional_returnsNull() {
        // Amounts are minor units (integers). Decimals must be rejected.
        assertNull(parseAmount("12.5"))
    }

    @Test
    fun parseAmount_overflow_returnsNull() {
        // Larger than Long.MAX_VALUE.
        assertNull(parseAmount("99999999999999999999"))
    }

    // MARK: - resolvePlacementType

    @Test
    fun resolvePlacementType_badge_returnsBadge() {
        assertEquals(PlacementType.BADGE, resolvePlacementType("CreditPromotionBadge"))
    }

    @Test
    fun resolvePlacementType_autoSize_returnsAutoSize() {
        assertEquals(PlacementType.AUTO_SIZE, resolvePlacementType("CreditPromotionAutoSize"))
    }

    @Test
    fun resolvePlacementType_null_defaultsToAutoSize() {
        assertEquals(PlacementType.AUTO_SIZE, resolvePlacementType(null))
    }

    @Test
    fun resolvePlacementType_unknown_defaultsToAutoSize() {
        assertEquals(PlacementType.AUTO_SIZE, resolvePlacementType("SomethingElse"))
    }

    @Test
    fun resolvePlacementType_caseSensitive() {
        // Only exact "CreditPromotionBadge" returns badge.
        assertEquals(PlacementType.AUTO_SIZE, resolvePlacementType("creditpromotionbadge"))
    }

    // MARK: - buildConfiguration

    @Test
    fun buildConfiguration_badge_returnsBadgeConfiguration() {
        val config = buildConfiguration("CreditPromotionBadge", KlarnaTheme.LIGHT, 1000L, "USD")
        assertTrue(
            "Expected CreditPromotionBadge but got ${config::class.simpleName}",
            config is KlarnaMessagingPlacementConfiguration.CreditPromotionBadge
        )
    }

    @Test
    fun buildConfiguration_autoSize_returnsAutoSizeConfiguration() {
        val config = buildConfiguration("CreditPromotionAutoSize", KlarnaTheme.DARK, 2000L, "EUR")
        assertTrue(
            "Expected CreditPromotionAutoSize but got ${config::class.simpleName}",
            config is KlarnaMessagingPlacementConfiguration.CreditPromotionAutoSize
        )
    }

    @Test
    fun buildConfiguration_null_defaultsToAutoSize() {
        val config = buildConfiguration(null, null, 100L, "SEK")
        assertTrue(config is KlarnaMessagingPlacementConfiguration.CreditPromotionAutoSize)
    }

    @Test
    fun buildConfiguration_unknown_defaultsToAutoSize() {
        val config = buildConfiguration("UnknownType", null, 100L, "SEK")
        assertTrue(config is KlarnaMessagingPlacementConfiguration.CreditPromotionAutoSize)
    }

    @Test
    fun buildConfiguration_passesNullThroughWhenThemeIsNull() {
        // Should not throw when theme is null.
        val config = buildConfiguration("CreditPromotionBadge", null, 100L, "GBP")
        assertTrue(config is KlarnaMessagingPlacementConfiguration.CreditPromotionBadge)
    }

    // MARK: - shouldReportHeight

    @Test
    fun shouldReportHeight_firstReport_isReported() {
        assertTrue(shouldReportHeight(null, 100))
    }

    @Test
    fun shouldReportHeight_changedHeight_isReported() {
        assertTrue(shouldReportHeight(100, 150))
    }

    @Test
    fun shouldReportHeight_sameHeight_isSkipped() {
        assertFalse(shouldReportHeight(100, 100))
    }

    @Test
    fun shouldReportHeight_zero_isSkipped() {
        assertFalse(shouldReportHeight(null, 0))
    }

    @Test
    fun shouldReportHeight_negative_isSkipped() {
        assertFalse(shouldReportHeight(null, -10))
    }

    @Test
    fun shouldReportHeight_zeroAfterValidHeight_isSkipped() {
        // A spurious 0 report must not clobber a previously reported height.
        assertFalse(shouldReportHeight(100, 0))
    }
}
