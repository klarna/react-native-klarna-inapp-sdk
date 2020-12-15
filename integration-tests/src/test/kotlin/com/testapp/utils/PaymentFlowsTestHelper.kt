package com.testapp.utils

import com.testapp.base.BaseAppiumTest
import com.testapp.constants.AppiumTestConstants
import com.testapp.extensions.deleteAll
import com.testapp.extensions.hideKeyboardCompat
import com.testapp.extensions.selectAll
import com.testapp.extensions.tapElementCenter
import io.appium.java_client.AppiumDriver
import io.appium.java_client.MobileElement
import io.appium.java_client.android.AndroidDriver
import io.appium.java_client.android.nativekey.AndroidKey
import io.appium.java_client.android.nativekey.KeyEvent
import io.appium.java_client.ios.IOSDriver
import io.appium.java_client.ios.IOSElement
import org.aspectj.weaver.ast.And
import org.json.JSONObject
import org.junit.Assert
import org.openqa.selenium.*
import org.openqa.selenium.support.ui.ExpectedConditions

internal object PaymentFlowsTestHelper {

    fun fillBillingAddress(driver: AppiumDriver<MobileElement>, billingInfo: LinkedHashMap<String, String?>) {
        if (driver is AndroidDriver) {
            // switch to klarna payment billing address iframe
            val billingWindow =
                    WebViewTestHelper.findWindowFor(driver, By.id("klarna-some-hardcoded-instance-id-fullscreen"))
            billingWindow?.let {
                driver.switchTo().window(it)
            } ?: Assert.fail("Billing address window wasn't found")
            DriverUtils.getWaiter(driver)
                    .until(ExpectedConditions.frameToBeAvailableAndSwitchToIt("klarna-some-hardcoded-instance-id-fullscreen"))
        }

        for ((key, value) in billingInfo) {
            value?.let {
                try {
                    fillInfo(driver, key, value)
                    if (driver is AndroidDriver) {
                        driver.pressKey(KeyEvent(AndroidKey.ENTER))
                    } else {
                        driver.hideKeyboardCompat()
                    }
                } catch (t: TimeoutException) {
                    // element is not visible (not required to fill)
                } catch (t: StaleElementReferenceException) {
                    // element is not attached
                }
            }
        }

        if (driver is AndroidDriver) {
            submitAndConfirm(driver, By.id("identification-dialog__footer-button-wrapper"), By.id("payinparts_kp-address-collection-dialog__footer-button-wrapper"))
        } else {
            submitAndConfirm(driver, By.xpath("//XCUIElementTypeButton[contains(@name,'Submit')]"), By.xpath("//XCUIElementTypeButton[contains(@name,'Continue anyway')]"))
            submitAndConfirm(driver, By.xpath("//XCUIElementTypeButton[contains(@name,'Continue anyway')]"), By.xpath("//XCUIElementTypeButton[contains(@name,'Continue')]"))
            submitAndConfirm(driver, By.xpath("//XCUIElementTypeButton[contains(@name,'Continue')]"), null)
        }
    }

    fun fillInfo(driver: AppiumDriver<MobileElement>, key: String, value: String?) {
        val element =
                DriverUtils.getWaiter(driver, 5).until(ExpectedConditions.presenceOfElementLocated(By.xpath(key)))
        element.apply {
            if (isEnabled || driver is IOSDriver) {
                driver.deleteAll(this)
                DriverUtils.getWaiter(BaseAppiumTest.driver)
                        .until(ExpectedConditions.invisibilityOfElementLocated(By.id("loading-overlay")))
                sendKeys(value)
                driver.hideKeyboardCompat()
            }
        }
    }

    fun submitAndConfirm(driver: AppiumDriver<MobileElement>, original: By, alternative: By?) {
        // click on the submit button
        var submitButtonBy: By? = null

        try {
            driver.findElement(original).click()
            submitButtonBy = original
        } catch (t: Throwable) {
            try {
                alternative?.let {
                    driver.findElement(it).click()
                    submitButtonBy = it
                }
            } catch (t: Throwable) {
            }
        }

        if (submitButtonBy == null) {
            return
        }

        // check if we should continue anyway
        var confirmPresent = true
        do {
            try {
                DriverUtils.wait(driver, 1)
                val continueAnyWay = driver.findElement(submitButtonBy)
                continueAnyWay.click()
            } catch (exception: Exception) {
                confirmPresent = false
            }
        } while (confirmPresent)
    }

    fun readConsoleMessage(driver: AppiumDriver<MobileElement>, containText: String): WebElement? {
        val by = if (driver is AndroidDriver) {
            By.xpath("//android.widget.TextView[contains(@text, '$containText')]")
        } else {
            By.xpath("//XCUIElementTypeOther[contains(@text, '$containText')]")
        }
        return DriverUtils.getWaiter(driver).until(ExpectedConditions.presenceOfElementLocated(by))
    }

    fun checkAuthorizeResponse(response: String?, successful: Boolean) {
        assert(!response.isNullOrBlank())
        val json = JSONObject(response!!.substring(response.indexOf("{")))
        if (successful) {
            assert(json.getBoolean("approved"))
            try {
                json.getString("authToken").toLowerCase().apply {
                    assert(!this.isBlank() && this != "null")
                }
            } catch (exception: Exception) {
                Assert.fail("Null authorization token.")
            }
        } else {
            Assert.assertFalse(json.getBoolean("approved"))
        }
    }

    fun fillCardInfo(driver: AppiumDriver<MobileElement>, is3ds: Boolean = false) {
        if (driver is AndroidDriver) {
            DriverUtils.getWaiter(driver).until(ExpectedConditions.presenceOfElementLocated(By.id("cardNumber")))
            driver.findElementById("cardNumber").sendKeys(if (is3ds) AppiumTestConstants.CARD_NUMBER_3DS else AppiumTestConstants.CARD_NUMBER)
            driver.findElementById("expire").sendKeys(AppiumTestConstants.CARD_EXPIREDATE)
            driver.findElementById("securityCode").sendKeys(AppiumTestConstants.CARD_CVV)
        } else {
            DriverUtils.getWaiter(driver).until(ExpectedConditions.presenceOfElementLocated(By.xpath("//XCUIElementTypeStaticText[@name='Card Number']")))
            driver.findElement(By.xpath("//XCUIElementTypeStaticText[@name='Card Number']")).apply {
                tapElementCenter()
                sendKeys(if (is3ds) AppiumTestConstants.CARD_NUMBER_3DS else AppiumTestConstants.CARD_NUMBER)
            }
            driver.findElement(By.xpath("//XCUIElementTypeStaticText[@name='MM/YY']")).apply {
                tapElementCenter()
                sendKeys(AppiumTestConstants.CARD_EXPIREDATE)
            }
            driver.findElement(By.xpath("//XCUIElementTypeStaticText[@name='CVC']")).apply {
                tapElementCenter()
                sendKeys(AppiumTestConstants.CARD_CVV)
            }
        }
    }

    fun fillSmsCode(driver: AppiumDriver<MobileElement>) {
        // if there is authentication for sms code
        try {
            DriverUtils.getWaiter(driver).until(ExpectedConditions.presenceOfElementLocated(By.id("authentication-ui")))
            DriverUtils.getWaiter(driver)
                    .until(ExpectedConditions.presenceOfElementLocated(By.id("otp-intro-send-button")))
                    .click()
            DriverUtils.getWaiter(driver)
                    .until(ExpectedConditions.presenceOfElementLocated(By.xpath("//input[@type='tel']"))).sendKeys("123456")
            DriverUtils.wait(driver, 2)
        } catch (t: Throwable) {
            // ignore
        }
    }

    fun dismissConsole() {
        try {
            readConsoleMessage(BaseAppiumTest.driver, "Dismiss All")?.click()
        } catch (t: Throwable) {
        }
    }
}