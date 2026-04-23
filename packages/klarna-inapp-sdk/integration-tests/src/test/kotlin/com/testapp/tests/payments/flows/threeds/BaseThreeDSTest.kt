package com.testapp.tests.payments.flows.threeds

import com.testapp.base.PaymentCategory
import com.testapp.extensions.hideKeyboardCompat
import com.testapp.extensions.tapElementCenter
import com.testapp.extensions.tryOptional
import com.testapp.network.KlarnaApi
import com.testapp.tests.payments.base.BasePaymentsTest
import com.testapp.utils.*
import io.appium.java_client.ios.IOSElement
import org.junit.jupiter.api.Assertions
import org.openqa.selenium.By
import org.openqa.selenium.support.ui.ExpectedConditions

internal abstract class BaseThreeDSTest : BasePaymentsTest() {

    protected fun test3ds(success: Boolean) {
        val session = KlarnaApi.getSessionInfo(SessionHelper.getRequestDE())?.session
        if (session?.client_token == null || !session.payment_method_categories.map { it.identifier }
                .contains(PaymentCategory.SLICE_IT.value)) {
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
        }

        DriverUtils.wait(driver, 5)
        PaymentFlowsTestHelper.fillCardInfo(driver, true)
        DriverUtils.switchContextToNative(driver)

        driver.hideKeyboardCompat()
        PaymentFlowsTestHelper.dismissConsole(driver)

        authorizeSDK(PaymentCategory.PAY_NOW)

        // enter billing address
        val billing = BillingAddressTestHelper.getBillingInfoDE()
        PaymentFlowsTestHelper.fillBillingAddress(driver, billing)

        val actionSelector: By
        if (android()) {
            val window = WebViewTestHelper.findWindowFor(driver, By.id("klarna-some-hardcoded-instance-id-fullscreen"))
            window?.let {
                driver.switchTo().window(it)
            } ?: Assertions.fail("3DS window wasn't found")
            DriverUtils.switchToIframe(driver, "klarna-some-hardcoded-instance-id-fullscreen")
            val frame = DriverUtils.waitForPresence(driver, By.id("3ds-dialog-iframe"))
            driver.switchTo().frame(frame)
            actionSelector = if (success) By.id("success") else By.id("rejected")
            DriverUtils.getWaiter(driver).until(
                ExpectedConditions.and(
                    ExpectedConditions.visibilityOfElementLocated(actionSelector),
                    ExpectedConditions.elementToBeClickable(actionSelector)
                )
            )
        } else {
            actionSelector =
                if (success) By.xpath("//XCUIElementTypeButton[@name='Success']") else By.xpath("//XCUIElementTypeButton[@name='3DS failure']")
            DriverUtils.wait(driver, 5)
        }

        var retryCount = 0
        var retries = 5
        while (retryCount < retries) {
            try {
                DriverUtils.getWaiter(driver).until(ExpectedConditions.presenceOfElementLocated(actionSelector)).click()
                break
            } catch (t: Throwable) {
                if (retryCount < retries - 1) {
                    DriverUtils.wait(driver, 5)
                    retryCount++
                } else {
                    throw t
                }
            }
        }

        if (success) {
            DriverUtils.switchContextToNative(driver)
            val response = PaymentFlowsTestHelper.readStateMessage(driver, PaymentCategory.PAY_NOW)
            PaymentFlowsTestHelper.checkAuthorizeResponse(response, true)
        } else {
            if (android()) {
                DriverUtils.switchToIframe(driver, "klarna-some-hardcoded-instance-id-fullscreen")
                val refusedTextBy = By.xpath("//*[@id=\"message-component-root\"]")
                val refusedText =
                    DriverUtils.getWaiter(driver).until(ExpectedConditions.presenceOfElementLocated(refusedTextBy))
                with(refusedText.text.toLowerCase()) {
                    assert(this.contains("sorry") || this.contains("unfortunately"))
                }
            } else {
                tryOptional {
                    DriverUtils.getWaiter(driver)
                        .until(ExpectedConditions.presenceOfElementLocated(By.xpath("//XCUIElementTypeButton[@name='Close']")))
                        .click()
                }
                val response = PaymentFlowsTestHelper.readStateMessage(driver, PaymentCategory.PAY_NOW)
                PaymentFlowsTestHelper.checkAuthorizeResponse(response, false)
            }
        }
    }
}