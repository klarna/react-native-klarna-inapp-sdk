package com.testapp.tests.browser

import com.testapp.base.PaymentCategory
import com.testapp.constants.AppiumTestConstants
import com.testapp.network.KlarnaApi
import com.testapp.utils.SessionHelper
import io.github.artsok.RepeatedIfExceptionsTest
import org.openqa.selenium.By

internal class BuiltInBrowserPayLaterTest : BaseBuiltInBrowserTest() {

    @RepeatedIfExceptionsTest(repeats = AppiumTestConstants.DEFAULT_RETRY_COUNT)
    fun `test if the terms links open the built-in browser - pay later uk`() {
        val session = KlarnaApi.getSessionInfo(SessionHelper.getRequestUK())?.session
        val by =
            if (android()) By.xpath("//a[contains(@aria-label, 'Privacy Notice')]") else By.xpath("//XCUIElementTypeLink[@name='Privacy Notice']")
        testLinks(session, PaymentCategory.PAY_LATER, by, null)
    }

    @RepeatedIfExceptionsTest(repeats = AppiumTestConstants.DEFAULT_RETRY_COUNT)
    fun `test if the terms links open the built-in browser - pay later sweden`() {
        val session = KlarnaApi.getSessionInfo(SessionHelper.getRequestSE())?.session
        val by =
            if (android()) By.xpath("//*[@id=\"invoice-privacy-notice\"]/span/a[1]") else By.xpath("//XCUIElementTypeLink[@name='Privacy Notice']")
        testLinks(session, PaymentCategory.PAY_LATER, by)
    }

    @RepeatedIfExceptionsTest(repeats = AppiumTestConstants.DEFAULT_RETRY_COUNT)
    fun `test if the terms links open the built-in browser - pay later germany`() {
        val session = KlarnaApi.getSessionInfo(SessionHelper.getRequestDE())?.session
        val by =
            if (android()) By.xpath("//*[@id=\"invoice-privacy-notice\"]/span/a[1]") else By.xpath("//XCUIElementTypeLink[@name='Privacy Notice']")
        testLinks(session, PaymentCategory.PAY_LATER, by)
    }

    @RepeatedIfExceptionsTest(repeats = AppiumTestConstants.DEFAULT_RETRY_COUNT)
    fun `test if the terms links open the built-in browser - pay later norway`() {
        val session = KlarnaApi.getSessionInfo(SessionHelper.getRequestNO())?.session
        val by =
            if (android()) By.xpath("//*[@id=\"invoice-privacy-notice\"]/span/a[1]") else By.xpath("//XCUIElementTypeLink[@name='Privacy Notice']")
        testLinks(session, PaymentCategory.PAY_LATER, by)
    }

    @RepeatedIfExceptionsTest(repeats = AppiumTestConstants.DEFAULT_RETRY_COUNT)
    fun `test if the terms links open the built-in browser - pay later finland`() {
        val session = KlarnaApi.getSessionInfo(SessionHelper.getRequestFI())?.session
        val by =
            if (android()) By.xpath("//*[@id=\"invoice-privacy-notice\"]/span/a[1]") else By.xpath("//XCUIElementTypeLink[@name='Privacy Notice']")
        testLinks(session, PaymentCategory.PAY_LATER, by)
    }

    @RepeatedIfExceptionsTest(repeats = AppiumTestConstants.DEFAULT_RETRY_COUNT)
    fun `test if the terms links open the built-in browser - pay later austria`() {
        val session = KlarnaApi.getSessionInfo(SessionHelper.getRequestAT())?.session
        val by =
            if (android()) By.xpath("//*[@id=\"invoice-privacy-notice\"]/span/a[1]") else By.xpath("//XCUIElementTypeLink[@name='Privacy Notice']")
        testLinks(session, PaymentCategory.PAY_LATER, by)
    }
}