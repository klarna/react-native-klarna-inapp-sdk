package com.testapp.tests.browser

import com.testapp.base.PaymentCategory
import com.testapp.network.KlarnaApi
import com.testapp.utils.SessionHelper
import org.junit.Test
import org.openqa.selenium.By

internal class BuiltInBrowserSliceItTest : BaseBuiltInBrowserTest() {

    @Test
    fun `test if the terms links open the built-in browser - slice it uk`() {
        val session = KlarnaApi.getSessionInfo(SessionHelper.getRequestUK())?.session
        val by =
            if (android()) By.xpath("//*[@id=\"pay-later-slice-it-slice-it-by-card-privacy-notice\"]/a[1]") else By.xpath(
                "//XCUIElementTypeLink[@name='Privacy Notice']"
            )
        testLinks(session, PaymentCategory.SLICE_IT, by)
    }

    @Test
    fun `test if the terms links open the built-in browser - slice it sweden`() {
        val session = KlarnaApi.getSessionInfo(SessionHelper.getRequestSE())?.session
        val by =
            if (android()) By.xpath("//*[@id=\"base-account-privacy-notice\"]/span/a[1]") else By.xpath("//XCUIElementTypeLink[@name='Privacy Notice']")
        testLinks(session, PaymentCategory.SLICE_IT, by)
    }

    @Test
    fun `test if the terms links open the built-in browser - slice it germany`() {
        val session = KlarnaApi.getSessionInfo(SessionHelper.getRequestDE())?.session
        val by =
            if (android()) By.xpath("//*[@id=\"base-account-privacy-notice\"]/span/a[1]") else By.xpath("//XCUIElementTypeLink[@name='Privacy Notice']")
        testLinks(session, PaymentCategory.SLICE_IT, by)
    }

    @Test
    fun `test if the terms links open the built-in browser - slice it norway`() {
        val session = KlarnaApi.getSessionInfo(SessionHelper.getRequestNO())?.session
        val by =
            if (android()) By.xpath("//*[@id=\"base-account-privacy-notice\"]/span/a[1]") else By.xpath("//XCUIElementTypeLink[@name='Privacy Notice']")
        testLinks(session, PaymentCategory.SLICE_IT, by)
    }

    @Test
    fun `test if the terms links open the built-in browser - slice it finland`() {
        val session = KlarnaApi.getSessionInfo(SessionHelper.getRequestFI())?.session
        val by =
            if (android()) By.xpath("//*[@id=\"base-account-privacy-notice\"]/span/a[1]") else By.xpath("//XCUIElementTypeLink[@name='Privacy Notice']")
        testLinks(session, PaymentCategory.SLICE_IT, by)
    }
}