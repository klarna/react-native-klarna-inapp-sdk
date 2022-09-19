package com.testapp.tests.payments.base

import com.testapp.base.BaseAppiumMethodTest
import com.testapp.base.PaymentCategory
import com.testapp.extensions.hideKeyboardCompat
import com.testapp.extensions.removeWhitespace
import com.testapp.extensions.tryOptional
import com.testapp.utils.ByRnId
import com.testapp.utils.DriverUtils
import com.testapp.utils.PaymentFlowsTestHelper
import io.appium.java_client.android.AndroidDriver
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.TestInfo
import org.openqa.selenium.support.ui.ExpectedConditions

internal abstract class BasePaymentsTest : BaseAppiumMethodTest() {

    @BeforeEach
    fun setup(testInfo: TestInfo) {
        DriverUtils.getWaiter(driver)
            .until(ExpectedConditions.presenceOfElementLocated(ByRnId(driver, "navKlarnaPayments")))
            .click()
    }

    fun initLoadSDK(token: String?, category: PaymentCategory) {
        DriverUtils.getWaiter(driver)
            .until(ExpectedConditions.presenceOfElementLocated(ByRnId(driver, "setTokenInput"))).apply {
                val trimmedToken = token?.removeWhitespace()
                sendKeys(trimmedToken)
                Assertions.assertEquals(trimmedToken, text?.removeWhitespace())
            }
        driver.hideKeyboardCompat()
        DriverUtils.wait(driver, 2)

        DriverUtils.getWaiter(driver)
            .until(ExpectedConditions.elementToBeClickable(ByRnId(driver, "initButton_${category.value}")))
            .click()
        DriverUtils.wait(driver, 1)
        DriverUtils.getWaiter(driver)
            .until(ExpectedConditions.elementToBeClickable(ByRnId(driver, "initButton_${category.value}")))
            .click()

        //wait for init response
        PaymentFlowsTestHelper.waitStateMessage(driver, category, if (ios()) "target" else "{}")

        DriverUtils.getWaiter(driver)
            .until(ExpectedConditions.elementToBeClickable(ByRnId(driver, "loadButton_${category.value}")))
            .click()
        DriverUtils.wait(driver, 1)

        tryOptional {
            driver.findElement(ByRnId(driver, "loadButton_${category.value}")).click()
        }
    }

    fun authorizeSDK(category: PaymentCategory) {
        val id = "authorizeButton_${category.value}"
        try {
            driver.findElement(ByRnId(driver, id)).click()
        } catch (t: Throwable) {
            (driver as? AndroidDriver<*>)?.findElementByAndroidUIAutomator("new UiScrollable(new UiSelector().scrollable(true).instance(0)).scrollIntoView(new UiSelector().description(\"$id\"))")
            DriverUtils.waitForPresence(driver, ByRnId(driver, id)).click()
        }
        DriverUtils.wait(driver, 2)
    }
}