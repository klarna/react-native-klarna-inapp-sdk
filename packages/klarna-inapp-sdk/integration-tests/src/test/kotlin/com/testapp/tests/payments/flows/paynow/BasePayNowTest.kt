package com.testapp.tests.payments.flows.paynow

import com.testapp.base.PaymentCategory
import com.testapp.extensions.hideKeyboardCompat
import com.testapp.extensions.tapElementCenter
import com.testapp.network.KlarnaApi
import com.testapp.tests.payments.base.BasePaymentsTest
import com.testapp.utils.*
import io.appium.java_client.ios.IOSElement
import org.junit.jupiter.api.Assertions
import org.openqa.selenium.By
import org.openqa.selenium.support.ui.ExpectedConditions

internal abstract class BasePayNowTest : BasePaymentsTest() {

    protected fun testPayNowSofort(success: Boolean) {
        val session = KlarnaApi.getSessionInfo(SessionHelper.getRequestDE())?.session
        if (session?.client_token == null || !session.payment_method_categories.map { it.identifier }
                .contains(PaymentCategory.PAY_NOW.value)) {
            return
        }
        val token = session.client_token
        initLoadSDK(token, PaymentCategory.PAY_NOW)

        if (android()) {
            DriverUtils.switchContextToWebView(driver)
            val mainWindow = WebViewTestHelper.findWindowFor(driver, By.id("klarna-some-hardcoded-instance-id-main"))
            mainWindow?.let {
                driver.switchTo().window(it)
            } ?: Assertions.fail("Main window wasn't found")
            DriverUtils.switchToIframe(driver, "klarna-some-hardcoded-instance-id-main")

            DriverUtils.getWaiter(driver)
                .until(ExpectedConditions.presenceOfElementLocated(By.id("installments-card|-1"))).click()

            DriverUtils.switchToIframe(driver, By.xpath("//*[@id=\"pay-now-card\"]//iframe"))

        } else {
            DriverUtils.getWaiter(driver)
                .until(ExpectedConditions.presenceOfElementLocated(By.xpath("//XCUIElementTypeOther[@name='Payment View']")))
            DriverUtils.getWaiter(driver)
                .until(ExpectedConditions.presenceOfElementLocated(By.xpath("//XCUIElementTypeStaticText[@name='TESTDRIVE']")))
            val card: IOSElement = DriverUtils.getWaiter(driver)
                .until(ExpectedConditions.presenceOfElementLocated(By.xpath("//XCUIElementTypeStaticText[@name='Card']"))) as IOSElement
            card.tapElementCenter(driver)
            DriverUtils.wait(driver, 5)
        }

        PaymentFlowsTestHelper.fillCardInfo(driver)

        DriverUtils.switchContextToNative(driver)

        driver.hideKeyboardCompat()

        PaymentFlowsTestHelper.dismissConsole(driver)

        authorizeSDK(PaymentCategory.PAY_NOW)

        val billing = BillingAddressTestHelper.getBillingInfoDE()
        if (!success) {
            BillingAddressTestHelper.setEmailFlag(billing, BillingAddressTestHelper.EMAIL_FLAG_REJECTED)
        }
        PaymentFlowsTestHelper.fillBillingAddress(driver, billing)

        if (!success) {
            if (android()) {
                val refusedTextBy = By.xpath("//*[@id=\"message-component-root\"]")
                val refusedText =
                    DriverUtils.getWaiter(driver).until(ExpectedConditions.presenceOfElementLocated(refusedTextBy))
                with(refusedText.text.toLowerCase()) {
                    assert(this.contains("sorry") || this.contains("unfortunately"))
                }
            } else {
                val changePaymentBy = By.xpath("//XCUIElementTypeButton[contains(@name, 'Change payment method')]")
                val changePayment =
                    DriverUtils.getWaiter(driver).until(ExpectedConditions.presenceOfElementLocated(changePaymentBy))
                changePayment.tapElementCenter(driver)
                val response = PaymentFlowsTestHelper.readStateMessage(driver, PaymentCategory.PAY_NOW)

                PaymentFlowsTestHelper.checkAuthorizeResponse(response, false)
            }

        } else {
            DriverUtils.switchContextToNative(driver)
            val response = PaymentFlowsTestHelper.readStateMessage(driver, PaymentCategory.PAY_NOW)

            PaymentFlowsTestHelper.checkAuthorizeResponse(response, true)
        }
    }
}