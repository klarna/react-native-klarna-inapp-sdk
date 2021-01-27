package com.testapp.tests.browser

import com.testapp.base.PaymentCategory
import com.testapp.network.KlarnaApi
import com.testapp.utils.SessionHelper
import org.junit.Test
import org.openqa.selenium.By

internal class BuiltInBrowserPayNowTest : BaseBuiltInBrowserTest() {

    @Test
    fun `test if the terms links open the built-in browser - pay now sweden`() {
        val session = KlarnaApi.getSessionInfo(SessionHelper.getRequestSE())?.session
        val by =
            if (android()) By.xpath("//*[@id=\"url-user_terms\"]") else By.xpath("//XCUIElementTypeLink[@name='Klarna Services Terms']")
        testLinks(session, PaymentCategory.PAY_NOW, by)
    }

    @Test
    fun `test if the terms links open the built-in browser - pay now germany`() {
        val session = KlarnaApi.getSessionInfo(SessionHelper.getRequestDE())?.session
        val by =
            if (android()) By.xpath("//*[@id=\"url-user_terms\"]") else By.xpath("//XCUIElementTypeLink[@name='Klarna Services Terms']")
        testLinks(session, PaymentCategory.PAY_NOW, by)
    }
}