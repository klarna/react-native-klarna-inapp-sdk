package com.testapp.tests.payments.flows.sliceit

import com.testapp.base.BaseAppiumTest
import com.testapp.base.PaymentCategory
import com.testapp.extensions.hideKeyboardCompat
import com.testapp.extensions.tapElementCenter
import com.testapp.extensions.tryOptional
import com.testapp.network.KlarnaApi
import com.testapp.utils.*
import io.appium.java_client.android.AndroidDriver
import org.junit.Assert
import org.junit.Ignore
import org.junit.Test
import org.openqa.selenium.By
import org.openqa.selenium.support.ui.ExpectedConditions

internal class SliceItUKTest : BaseAppiumTest() {

    @Test
    fun `test payment slice it UK successful flow`() {
        testSliceItUK(true)
    }

    @Test
    @Ignore
    fun `test payment slice it UK failure flow`() {
        testSliceItUK(false)
    }

    private fun testSliceItUK(success: Boolean) {
        val session = KlarnaApi.getSessionInfo(SessionHelper.getRequestUK())?.session
        if (session?.client_token == null || !session.payment_method_categories.map { it.identifier }
                .contains(PaymentCategory.SLICE_IT.value)) {
            return
        }
        val token = session.client_token
        initLoadSDK(token, PaymentCategory.SLICE_IT)
        var mainWindow: String? = null
        if (android()) {
            DriverUtils.switchContextToWebView(driver)
            mainWindow = WebViewTestHelper.findWindowFor(driver, By.id("klarna-some-hardcoded-instance-id-main"))
            mainWindow?.let {
                driver.switchTo().window(it)
            } ?: Assert.fail("Main window wasn't found")
            DriverUtils.switchToIframe(driver, "klarna-some-hardcoded-instance-id-main")
            DriverUtils.waitForPresence(driver, By.id("scheme-payment-selector"))
//
//        PaymentFlowsTestHelper.fillCardInfo(driver)

            DriverUtils.switchContextToNative(driver)
        } else {
            DriverUtils.waitForPresence(driver, By.xpath("//XCUIElementTypeOther[@name='Payment View']"))
            DriverUtils.waitForPresence(driver, By.xpath("//XCUIElementTypeStaticText[@name='TESTDRIVE']"))
        }

        driver.hideKeyboardCompat()

        PaymentFlowsTestHelper.dismissConsole(driver)

        try {
            driver.findElement(ByRnId(driver, "authorizeButton_${PaymentCategory.SLICE_IT.value}")).click()
        } catch (t: Throwable) {
            (driver as? AndroidDriver<*>)?.let { driver ->
                driver.findElementByAndroidUIAutomator("new UiScrollable(new UiSelector().scrollable(true).instance(0)).scrollIntoView(new UiSelector().description(\"authorizeButton_${PaymentCategory.SLICE_IT.value}\"))")
            }
            DriverUtils.waitForPresence(driver, ByRnId(driver, "authorizeButton_${PaymentCategory.SLICE_IT.value}")).click()
        }

        val billing = BillingAddressTestHelper.getBillingInfoUK()
        if (!success) {
            BillingAddressTestHelper.setEmailFlag(billing, BillingAddressTestHelper.EMAIL_FLAG_REJECTED)
        }

        val verificationWindow = WebViewTestHelper.findWindowFor(driver, By.id("klarna-some-hardcoded-instance-id-fullscreen"), By.id("email_or_phone"))
        verificationWindow?.let {
            driver.switchTo().window(it)
            DriverUtils.switchToIframe(driver, "klarna-some-hardcoded-instance-id-fullscreen")
            DriverUtils.waitForPresence(driver, By.ById("email_or_phone")).sendKeys(billing.options.phone)
            DriverUtils.waitForPresence(driver, By.ById("btn-continue")).click()
            DriverUtils.waitForPresence(driver, By.ById("otp_field")).sendKeys("123456")
            tryOptional {
                DriverUtils.waitForPresence(driver, By.ById("email_or_phone"), 5).sendKeys(billing.options.email)
                DriverUtils.waitForPresence(driver, By.ById("btn-continue"), 5).click()
                DriverUtils.waitForPresence(driver, By.ById("otp_field"), 5).sendKeys("123456")
            }
            DriverUtils.wait(driver, 1)
        }

        try {
            DriverUtils.waitForPresence(driver, By.id("payinparts_kp-purchase-review-continue-button"), 5).click()
        } catch (t: Throwable) {
            PaymentFlowsTestHelper.fillBillingAddress(driver, billing)
        }

        /*PaymentFlowsTestHelper.fillSmsCode(driver)

        tryOptional {
            PaymentFlowsTestHelper.fillInfo(driver, billing, billing.identifiers.birthday2, billing.options.birthday)
        }

        if (android()) {
            PaymentFlowsTestHelper.submitAndConfirm(
                driver,
                By.xpath("//button[contains(@id,'purchase-approval-form-continue-button')]"),
                By.xpath("//div[contains(@id,'purchase-approval__footer-button-wrapper')]")
            )
        } else {
            driver.hideKeyboardCompat()
            PaymentFlowsTestHelper.submitAndConfirm(
                driver,
                By.xpath("//XCUIElementTypeButton[contains(@name,'Confirm')]"),
                null
            )
        }*/

        driver.switchTo().window(mainWindow)
        if (!success) {
            if (android()) {
                DriverUtils.waitForPresence(
                    driver,
                    By.xpath("//h1[contains(text(),'Your application was declined')]")
                )
            } else {
                val changePaymentBy = By.xpath("//XCUIElementTypeButton[contains(@name, 'Change payment method')]")
                val changePayment = DriverUtils.waitForPresence(driver, changePaymentBy)
                changePayment.tapElementCenter(driver)
                val response = PaymentFlowsTestHelper.readStateMessage(driver, PaymentCategory.SLICE_IT)
                PaymentFlowsTestHelper.checkAuthorizeResponse(response, false)
            }
        } else {
            DriverUtils.switchContextToNative(driver)
            val response = PaymentFlowsTestHelper.readStateMessage(driver, PaymentCategory.SLICE_IT)

            PaymentFlowsTestHelper.checkAuthorizeResponse(response, true)
        }
    }
}
