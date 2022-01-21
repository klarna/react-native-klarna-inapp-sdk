package com.testapp.tests.payments.flows.paylater

import com.testapp.base.BaseAppiumTest
import com.testapp.base.PaymentCategory
import com.testapp.extensions.tapElementCenter
import com.testapp.extensions.tryOptional
import com.testapp.model.Session
import com.testapp.utils.*
import io.appium.java_client.android.AndroidDriver
import org.junit.Assert
import org.openqa.selenium.By
import org.openqa.selenium.support.ui.ExpectedConditions

internal abstract class BasePayLaterTest : BaseAppiumTest() {

    protected fun testPayLater(success: Boolean, session: Session?, billing: BillingInfo) {
        if (session?.client_token == null || !session.payment_method_categories.map { it.identifier }.contains(
                PaymentCategory.PAY_LATER.value
            )
        ) {
            return
        }
        val token = session.client_token

        initLoadSDK(token, PaymentCategory.PAY_LATER)
        if (android()) {
            DriverUtils.switchContextToWebView(driver)
            val mainWindow =
                WebViewTestHelper.findWindowFor(driver, By.id("klarna-some-hardcoded-instance-id-fullscreen"))
            mainWindow?.let {
                driver.switchTo().window(it)
            } ?: Assert.fail("Main window wasn't found")
            DriverUtils.switchToIframe(driver, "klarna-some-hardcoded-instance-id-fullscreen")
            DriverUtils.switchContextToNative(driver)
        } else {
            DriverUtils.getWaiter(driver)
                .until(ExpectedConditions.presenceOfElementLocated(By.xpath("//XCUIElementTypeOther[@name='Payment View']")))
            DriverUtils.getWaiter(driver)
                .until(ExpectedConditions.presenceOfElementLocated(By.xpath("//XCUIElementTypeStaticText[@name='TESTDRIVE']")))
        }

        PaymentFlowsTestHelper.dismissConsole(driver)

        authorizeSDK(PaymentCategory.PAY_LATER)

        if (!success) {
            BillingAddressTestHelper.setEmailFlag(billing, BillingAddressTestHelper.EMAIL_FLAG_REJECTED)
        }
        PaymentFlowsTestHelper.fillBillingAddress(driver, billing)

        if (!success) {
            if (android()) {
                val refusedTextBy = By.xpath("//*[contains(@id,'message-component-root') or contains(text(),'Unfortunately') or contains(text(),'sorry')]")
                val refusedText =
                    DriverUtils.getWaiter(driver).until(ExpectedConditions.presenceOfElementLocated(refusedTextBy))
                with(refusedText.text.toLowerCase()) {
                    assert(this.contains("sorry") || this.contains("unfortunately"))
                }
            } else {
                try {
                    val changePaymentBy = By.xpath("//XCUIElementTypeButton[contains(@name, 'Change payment method')]")
                    val changePayment = DriverUtils.getWaiter(driver)
                        .until(ExpectedConditions.presenceOfElementLocated(changePaymentBy))
                    changePayment.tapElementCenter(driver)
                } catch (t: Throwable) {
                    val closeBy = By.xpath("//XCUIElementTypeButton[contains(@name, 'Close')]")
                    val close =
                        DriverUtils.getWaiter(driver).until(ExpectedConditions.presenceOfElementLocated(closeBy))
                    close.tapElementCenter(driver)
                }
                val response = PaymentFlowsTestHelper.readStateMessage(driver, PaymentCategory.PAY_LATER)
                PaymentFlowsTestHelper.checkAuthorizeResponse(response, false)
            }
        } else {
            if (android()) tryOptional {
                val submitButtonBy = By.xpath("//*[@id=\"confirmation__footer-button-wrapper\"]/div/button")
                DriverUtils.getWaiter(driver).until(ExpectedConditions.presenceOfElementLocated(submitButtonBy)).click()
            }
            DriverUtils.switchContextToNative(driver)
            val response = PaymentFlowsTestHelper.readStateMessage(driver, PaymentCategory.PAY_LATER)

            PaymentFlowsTestHelper.checkAuthorizeResponse(response, true)
        }
    }
}
