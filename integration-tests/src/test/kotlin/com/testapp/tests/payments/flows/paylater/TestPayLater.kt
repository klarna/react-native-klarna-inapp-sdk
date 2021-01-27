package com.testapp.tests.payments.flows.paylater

import com.testapp.base.BaseAppiumTest
import com.testapp.base.PaymentCategory
import com.testapp.base.RetryRule
import com.testapp.extensions.tapElementCenter
import com.testapp.model.Session
import com.testapp.network.KlarnaApi
import com.testapp.utils.*
import io.appium.java_client.MobileElement
import io.appium.java_client.android.AndroidDriver
import org.junit.Assert
import org.junit.BeforeClass
import org.junit.Rule
import org.junit.Test
import org.openqa.selenium.By
import org.openqa.selenium.support.ui.ExpectedConditions

internal class TestPayLater : BaseAppiumTest() {

    @Test
    fun `test payment pay later sweden successful flow`() {
        val session = KlarnaApi.getSessionInfo(SessionHelper.getRequestSE())?.session
        testPayLater(true, session, BillingAddressTestHelper.getBillingInfoSE())
    }

    @Test
    fun `test payment pay later sweden failure flow`() {
        val session = KlarnaApi.getSessionInfo(SessionHelper.getRequestSE())?.session
        testPayLater(false, session, BillingAddressTestHelper.getBillingInfoSE())
    }

    @Test
    fun `test payment pay later norway successful flow`() {
        val session = KlarnaApi.getSessionInfo(SessionHelper.getRequestNO())?.session
        testPayLater(true, session, BillingAddressTestHelper.getBillingInfoNO())
    }

    @Test
    fun `test payment pay later norway failure flow`() {
        val session = KlarnaApi.getSessionInfo(SessionHelper.getRequestNO())?.session
        testPayLater(false, session, BillingAddressTestHelper.getBillingInfoNO())
    }

    @Test
    fun `test payment pay later finland successful flow`() {
        val session = KlarnaApi.getSessionInfo(SessionHelper.getRequestFI())?.session
        testPayLater(true, session, BillingAddressTestHelper.getBillingInfoFI())
    }

    @Test
    fun `test payment pay later finland failure flow`() {
        val session = KlarnaApi.getSessionInfo(SessionHelper.getRequestFI())?.session
        testPayLater(false, session, BillingAddressTestHelper.getBillingInfoFI())
    }

    @Test
    fun `test payment pay later germany successful flow`() {
        val session = KlarnaApi.getSessionInfo(SessionHelper.getRequestDE())?.session
        testPayLater(true, session, BillingAddressTestHelper.getBillingInfoDE())
    }

    @Test
    fun `test payment pay later germany failure flow`() {
        val session = KlarnaApi.getSessionInfo(SessionHelper.getRequestDE())?.session
        testPayLater(false, session, BillingAddressTestHelper.getBillingInfoDE())
    }

    private fun testPayLater(success: Boolean, session: Session?, billing: BillingInfo){
        if(session?.client_token == null || !session.payment_method_categories.map { it.identifier }.contains(PaymentCategory.PAY_LATER.value)){
            return
        }
        val token = session.client_token

        initLoadSDK(token, PaymentCategory.PAY_LATER.value)
        if (android()) {
            DriverUtils.switchContextToWebView(driver)
            val mainWindow = WebViewTestHelper.findWindowFor(driver, By.id("klarna-some-hardcoded-instance-id-fullscreen"))
            mainWindow?.let {
                driver.switchTo().window(it)
            } ?: Assert.fail("Main window wasn't found")
            DriverUtils.getWaiter(driver).until(ExpectedConditions.frameToBeAvailableAndSwitchToIt("klarna-some-hardcoded-instance-id-fullscreen"))
            DriverUtils.switchContextToNative(driver)
        } else {
            DriverUtils.getWaiter(driver).until(ExpectedConditions.presenceOfElementLocated(By.xpath("//XCUIElementTypeOther[@name='Payment View']")))
        }

        PaymentFlowsTestHelper.dismissConsole(driver)

        try {
            driver.findElement(ByRnId(driver, "authorizeButton_${PaymentCategory.PAY_LATER.value}")).click()
        } catch (t: Throwable) {
            (driver as? AndroidDriver<*>)?.let { driver ->
                driver.findElementByAndroidUIAutomator("new UiScrollable(new UiSelector().scrollable(true).instance(0)).scrollIntoView(new UiSelector().description(\"authorizeButton_${PaymentCategory.PAY_LATER.value}\"))")
            }
            DriverUtils.getWaiter(driver).until(ExpectedConditions.presenceOfElementLocated(ByRnId(driver, "authorizeButton_${PaymentCategory.PAY_LATER.value}"))).click()
        }

        if(!success){
            BillingAddressTestHelper.setEmailFlag(billing, BillingAddressTestHelper.EMAIL_FLAG_REJECTED)
        }
        PaymentFlowsTestHelper.fillBillingAddress(driver, billing)

        if(!success) {
            if (android()) {
                val refusedTextBy = By.xpath("//*[@id=\"message-component-root\"]")
                val refusedText =
                        DriverUtils.getWaiter(driver).until(ExpectedConditions.presenceOfElementLocated(refusedTextBy))
                with(refusedText.text.toLowerCase()) {
                    assert(this.contains("sorry") || this.contains("unfortunately"))
                }
            } else {
                try {
                    val changePaymentBy = By.xpath("//XCUIElementTypeButton[contains(@name, 'Change payment method')]")
                    val changePayment = DriverUtils.getWaiter(driver).until(ExpectedConditions.presenceOfElementLocated(changePaymentBy))
                    (changePayment as MobileElement).tapElementCenter(driver)
                } catch (t:Throwable) {
                    val closeBy = By.xpath("//XCUIElementTypeButton[contains(@name, 'Close')]")
                    val close = DriverUtils.getWaiter(driver).until(ExpectedConditions.presenceOfElementLocated(closeBy))
                    (close as MobileElement).tapElementCenter(driver)
                }
                val response = PaymentFlowsTestHelper.readStateMessage(driver, PaymentCategory.PAY_LATER)
                PaymentFlowsTestHelper.checkAuthorizeResponse(response, false)
            }
        } else{
            if (android()) {
                val submitButtonBy = By.xpath("//*[@id=\"confirmation__footer-button-wrapper\"]/div/button")
                DriverUtils.getWaiter(driver).until(ExpectedConditions.presenceOfElementLocated(submitButtonBy)).click()
            }
            DriverUtils.switchContextToNative(driver)
            val response = PaymentFlowsTestHelper.readStateMessage(driver, PaymentCategory.PAY_LATER)

            PaymentFlowsTestHelper.checkAuthorizeResponse(response, true)
        }
    }
}