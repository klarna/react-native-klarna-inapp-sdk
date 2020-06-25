package com.testapp.tests.browser

import com.testapp.base.BaseAppiumTest
import com.testapp.base.PaymentCategory
import com.testapp.model.Session
import com.testapp.network.KlarnaApi
import com.testapp.utils.*
import io.appium.java_client.MobileBy
import io.appium.java_client.android.AndroidDriver
import org.junit.Assert
import org.junit.BeforeClass
import org.junit.Test
import org.openqa.selenium.By
import org.openqa.selenium.support.ui.ExpectedConditions

internal class TestBuiltInBrowser : BaseAppiumTest() {
    companion object {

        @JvmStatic
        @BeforeClass
        fun setup() {
            testName = TestBuiltInBrowser::class.java.simpleName
            BaseAppiumTest.setup()
        }
    }

    @Test
    fun `test if the terms links open the built-in browser - pay later uk`() {
        val session = KlarnaApi.getSessionInfo(SessionHelper.getRequestUK())?.session
        testLinks(session, PaymentCategory.PAY_LATER, By.xpath("//*[@id=\"invoice-privacy-notice\"]/span/a[1]"))
    }

    @Test
    fun `test if the terms links open the built-in browser - pay later sweden`() {
        val session = KlarnaApi.getSessionInfo(SessionHelper.getRequestSE())?.session
        testLinks(session, PaymentCategory.PAY_LATER, By.xpath("//*[@id=\"invoice-privacy-notice\"]/span/a[1]"))
    }

    @Test
    fun `test if the terms links open the built-in browser - pay later germany`() {
        val session = KlarnaApi.getSessionInfo(SessionHelper.getRequestDE())?.session
        testLinks(session, PaymentCategory.PAY_LATER, By.xpath("//*[@id=\"invoice-privacy-notice\"]/span/a[1]"))
    }

    @Test
    fun `test if the terms links open the built-in browser - pay later norway`() {
        val session = KlarnaApi.getSessionInfo(SessionHelper.getRequestNO())?.session
        testLinks(session, PaymentCategory.PAY_LATER, By.xpath("//*[@id=\"invoice-privacy-notice\"]/span/a[1]"))
    }

    @Test
    fun `test if the terms links open the built-in browser - pay later finland`() {
        val session = KlarnaApi.getSessionInfo(SessionHelper.getRequestFI())?.session
        testLinks(session, PaymentCategory.PAY_LATER, By.xpath("//*[@id=\"pay-later-privacy-notice\"]/span/a[1]"))
    }

    @Test
    fun `test if the terms links open the built-in browser - pay later austria`() {
        val session = KlarnaApi.getSessionInfo(SessionHelper.getRequestAT())?.session
        testLinks(session, PaymentCategory.PAY_LATER, By.xpath("//*[@id=\"invoice-privacy-notice\"]/span/a[1]"))
    }

    @Test
    fun `test if the terms links open the built-in browser - pay now sweden`() {
        val session = KlarnaApi.getSessionInfo(SessionHelper.getRequestSE())?.session
        testLinks(session, PaymentCategory.PAY_NOW, By.xpath("//*[@id=\"url-user_terms\"]"))
    }

    @Test
    fun `test if the terms links open the built-in browser - pay now germany`() {
        val session = KlarnaApi.getSessionInfo(SessionHelper.getRequestDE())?.session
        testLinks(session, PaymentCategory.PAY_NOW, By.xpath("//*[@id=\"url-user_terms\"]"))
    }

    @Test
    fun `test if the terms links open the built-in browser - slice it uk`() {
        val session = KlarnaApi.getSessionInfo(SessionHelper.getRequestUK())?.session
        testLinks(session, PaymentCategory.SLICE_IT, By.xpath("//*[@id=\"pay-later-slice-it-slice-it-by-card-privacy-notice\"]/a[1]"))
    }

    @Test
    fun `test if the terms links open the built-in browser - slice it sweden`() {
        val session = KlarnaApi.getSessionInfo(SessionHelper.getRequestSE())?.session
        testLinks(session, PaymentCategory.SLICE_IT, By.xpath("//*[@id=\"base-account-privacy-notice\"]/span/a[1]"))
    }

    @Test
    fun `test if the terms links open the built-in browser - slice it germany`() {
        val session = KlarnaApi.getSessionInfo(SessionHelper.getRequestDE())?.session
        testLinks(session, PaymentCategory.SLICE_IT, By.xpath("//*[@id=\"base-account-privacy-notice\"]/span/a[1]"))
    }

    @Test
    fun `test if the terms links open the built-in browser - slice it norway`() {
        val session = KlarnaApi.getSessionInfo(SessionHelper.getRequestNO())?.session
        testLinks(session, PaymentCategory.SLICE_IT, By.xpath("//*[@id=\"base-account-privacy-notice\"]/span/a[1]"))
    }

    @Test
    fun `test if the terms links open the built-in browser - slice it finland`() {
        val session = KlarnaApi.getSessionInfo(SessionHelper.getRequestFI())?.session
        testLinks(session, PaymentCategory.SLICE_IT, By.xpath("//*[@id=\"base-account-privacy-notice\"]/span/a[1]"))
    }

    private fun testLinks(session: Session?, category: PaymentCategory, linkLocator: By) {

        if(session?.client_token == null || !session.payment_method_categories.map { it.identifier }.contains(category.value)){
            return
        }
        val token = session.client_token

        DriverUtils.switchContextToNative(driver)
        DriverUtils.getWaiter(driver).until(ExpectedConditions.presenceOfElementLocated(ByRnId(driver, "setTokenInput"))).apply {
            sendKeys(token)
            Assert.assertEquals(token, text)
        }
        driver.findElement(ByRnId(driver,"initButton_${category.value}")).click()
        //wait for init response
        PaymentFlowsTestHelper.readConsoleMessage(driver, "{}")

        driver.findElement(ByRnId(driver,"loadButton_${category.value}")).click()
        DriverUtils.switchContextToWebView(driver)
        val mainWindow = WebViewTestHelper.findWindowFor(driver, By.id("klarna-some-hardcoded-instance-id-main"))
        mainWindow?.let {
            driver.switchTo().window(it)
        } ?: Assert.fail("Main window wasn't found")
        DriverUtils.getWaiter(driver).until(ExpectedConditions.frameToBeAvailableAndSwitchToIt("klarna-some-hardcoded-instance-id-main"))

        var tries = 0
        var openedLink = false
        do {
            try {
                DriverUtils.wait(driver, 1)
                val links = DriverUtils.getWaiter(driver)
                        .until(ExpectedConditions.presenceOfAllElementsLocatedBy(linkLocator)).filter { !it.text.isNullOrEmpty() }
                links.first().click()
                DriverUtils.waitForActivity(driver as AndroidDriver<*>, "com.klarna.mobile.sdk.core.natives.browser.ui.InternalBrowserActivity", 2)
                DriverUtils.switchContextToNative(driver)
                driver.findElement(MobileBy.id("closeIcon")).click()
                DriverUtils.switchContextToWebView(driver)
                openedLink = true
            } catch (t: Throwable){
                tries++
            }
        } while (!openedLink && tries < 3)
    }
}