package com.klarna.mobile.sdk.reactnative.klarnanetwork.payment

import com.klarna.mobile.sdk.api.KlarnaTheme
import com.klarna.mobile.sdk.api.button.KlarnaButtonShape
import com.klarna.mobile.sdk.api.button.KlarnaButtonStyle
import com.klarna.mobile.sdk.klarna.network.core.api.button.KlarnaButtonState
import com.klarna.mobile.sdk.klarna.network.payment.button.api.KlarnaPaymentButtonIntent
import org.junit.Before
import org.junit.Test
import kotlin.test.assertEquals

class KlarnaPaymentButtonViewManagerTest {

    private lateinit var manager: KlarnaPaymentButtonViewManager

    @Before
    fun setUp() {
        manager = KlarnaPaymentButtonViewManager()
    }

    // region parseState

    @Test
    fun `parseState - returns DEFAULT for null`() {
        assertEquals(KlarnaButtonState.DEFAULT, manager.parseState(null))
    }

    @Test
    fun `parseState - returns DISABLED for disabled`() {
        assertEquals(KlarnaButtonState.DISABLED, manager.parseState("disabled"))
    }

    @Test
    fun `parseState - returns LOADING for loading`() {
        assertEquals(KlarnaButtonState.LOADING, manager.parseState("loading"))
    }

    @Test
    fun `parseState - returns DEFAULT for unknown value`() {
        assertEquals(KlarnaButtonState.DEFAULT, manager.parseState("unknown"))
    }

    // endregion

    // region parseIntent

    @Test
    fun `parseIntent - returns PAY for null`() {
        assertEquals(KlarnaPaymentButtonIntent.PAY, manager.parseIntent(null))
    }

    @Test
    fun `parseIntent - returns PAY for pay`() {
        assertEquals(KlarnaPaymentButtonIntent.PAY, manager.parseIntent("pay"))
    }

    @Test
    fun `parseIntent - returns SUBSCRIBE for subscribe`() {
        assertEquals(KlarnaPaymentButtonIntent.SUBSCRIBE, manager.parseIntent("subscribe"))
    }

    @Test
    fun `parseIntent - returns ADD_TO_WALLET for addToWallet`() {
        assertEquals(KlarnaPaymentButtonIntent.ADD_TO_WALLET, manager.parseIntent("addToWallet"))
    }

    @Test
    fun `parseIntent - returns PAY for unknown value`() {
        assertEquals(KlarnaPaymentButtonIntent.PAY, manager.parseIntent("unknown"))
    }

    // endregion

    // region parseShape

    @Test
    fun `parseShape - returns ROUNDED_RECT for null`() {
        assertEquals(KlarnaButtonShape.ROUNDED_RECT, manager.parseShape(null))
    }

    @Test
    fun `parseShape - returns ROUNDED_RECT for roundedRect`() {
        assertEquals(KlarnaButtonShape.ROUNDED_RECT, manager.parseShape("roundedRect"))
    }

    @Test
    fun `parseShape - returns PILL for pill`() {
        assertEquals(KlarnaButtonShape.PILL, manager.parseShape("pill"))
    }

    @Test
    fun `parseShape - returns RECTANGLE for rectangle`() {
        assertEquals(KlarnaButtonShape.RECTANGLE, manager.parseShape("rectangle"))
    }

    @Test
    fun `parseShape - returns ROUNDED_RECT for unknown value`() {
        assertEquals(KlarnaButtonShape.ROUNDED_RECT, manager.parseShape("unknown"))
    }

    // endregion

    // region parseStyle

    @Test
    fun `parseStyle - returns FILLED for null`() {
        assertEquals(KlarnaButtonStyle.FILLED, manager.parseStyle(null))
    }

    @Test
    fun `parseStyle - returns FILLED for filled`() {
        assertEquals(KlarnaButtonStyle.FILLED, manager.parseStyle("filled"))
    }

    @Test
    fun `parseStyle - returns OUTLINED for outlined`() {
        assertEquals(KlarnaButtonStyle.OUTLINED, manager.parseStyle("outlined"))
    }

    @Test
    fun `parseStyle - returns FILLED for unknown value`() {
        assertEquals(KlarnaButtonStyle.FILLED, manager.parseStyle("unknown"))
    }

    // endregion

    // region parseTheme

    @Test
    fun `parseTheme - returns DARK for null`() {
        assertEquals(KlarnaTheme.DARK, manager.parseTheme(null))
    }

    @Test
    fun `parseTheme - returns LIGHT for light`() {
        assertEquals(KlarnaTheme.LIGHT, manager.parseTheme("light"))
    }

    @Test
    fun `parseTheme - returns DARK for dark`() {
        assertEquals(KlarnaTheme.DARK, manager.parseTheme("dark"))
    }

    @Test
    fun `parseTheme - returns AUTOMATIC for automatic`() {
        assertEquals(KlarnaTheme.AUTOMATIC, manager.parseTheme("automatic"))
    }

    @Test
    fun `parseTheme - returns DARK for unknown value`() {
        assertEquals(KlarnaTheme.DARK, manager.parseTheme("unknown"))
    }

    // endregion
}
