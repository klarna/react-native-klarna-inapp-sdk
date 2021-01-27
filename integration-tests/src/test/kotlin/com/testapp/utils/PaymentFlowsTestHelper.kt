package com.testapp.utils

import com.testapp.base.PaymentCategory
import com.testapp.constants.AppiumTestConstants
import com.testapp.extensions.*
import io.appium.java_client.AppiumDriver
import io.appium.java_client.MobileElement
import io.appium.java_client.android.AndroidDriver
import io.appium.java_client.android.nativekey.AndroidKey
import io.appium.java_client.android.nativekey.KeyEvent
import io.appium.java_client.ios.IOSDriver
import io.appium.java_client.ios.IOSElement
import org.json.JSONObject
import org.junit.Assert
import org.openqa.selenium.By
import org.openqa.selenium.StaleElementReferenceException
import org.openqa.selenium.TimeoutException
import org.openqa.selenium.WebElement
import org.openqa.selenium.support.ui.ExpectedConditions

internal object PaymentFlowsTestHelper {

    fun fillBillingAddress(driver: AppiumDriver<MobileElement>, billingInfo: BillingInfo) {
        if (driver is AndroidDriver) {
            DriverUtils.switchContextToWebView(driver)
            // switch to klarna payment billing address iframe
            val billingWindow =
                WebViewTestHelper.findWindowFor(driver, By.id("klarna-some-hardcoded-instance-id-fullscreen"))
            billingWindow?.let {
                driver.switchTo().window(it)
            } ?: Assert.fail("Billing address window wasn't found")
            DriverUtils.getWaiter(driver)
                .until(ExpectedConditions.frameToBeAvailableAndSwitchToIt("klarna-some-hardcoded-instance-id-fullscreen"))
        }

        for ((key, value) in billingInfo.linkedMap()) {
            value?.let {
                try {
                    if (driver is IOSDriver) {
                        driver.scroll()
                    }
                    fillInfo(driver, billingInfo, key, value)
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
            submitAndConfirm(
                driver,
                By.id("identification-dialog__footer-button-wrapper"),
                By.id("payinparts_kp-address-collection-dialog__footer-button-wrapper")
            )
        } else {
            submitAndConfirm(
                driver,
                By.xpath("//XCUIElementTypeButton[contains(@name,'Submit')]"),
                By.xpath("//XCUIElementTypeButton[contains(@name,'Continue anyway')]")
            )
            submitAndConfirm(
                driver,
                By.xpath("//XCUIElementTypeButton[contains(@name,'Continue anyway')]"),
                By.xpath("//XCUIElementTypeButton[contains(@name,'Continue')]")
            )
            submitAndConfirm(
                driver,
                By.xpath("//XCUIElementTypeButton[contains(@name,'Continue')]"),
                By.xpath("//XCUIElementTypeButton[contains(@name,'Confirm')]")
            )
        }
    }

    fun fillInfo(driver: AppiumDriver<MobileElement>, billingInfo: BillingInfo, key: String, value: String?) {
        val element = DriverUtils.getWaiter(driver, 5)
            .until(ExpectedConditions.presenceOfElementLocated(By.xpath(key)))
        element.apply {
            if (isEnabled || driver is IOSDriver) {
                DriverUtils.getWaiter(driver)
                    .until(ExpectedConditions.invisibilityOfElementLocated(By.id("loading-overlay")))
                if (this is IOSElement) {
                    tapElementCenter(driver)
                    DriverUtils.wait(driver, 5)

                    if (billingInfo.identifiers.title == key) {
                        DriverUtils.getWaiter(driver)
                            .until(
                                ExpectedConditions.presenceOfElementLocated(
                                    By.xpath("//XCUIElementTypePickerWheel")
                                )
                            ).sendKeys(value)
                        tryOptional {
                            DriverUtils.getWaiter(driver)
                                .until(
                                    ExpectedConditions.presenceOfElementLocated(
                                        By.xpath("//XCUIElementTypeButton[@name='Done']")
                                    )
                                ).click()
                        }
                    }
                    if (driver.isKeyboardVisible()) {
                        sendKeys(value)
                    }
                } else {
                    if (billingInfo.identifiers.title == key) {
                        DriverUtils.getWaiter(driver)
                            .until(
                                ExpectedConditions.presenceOfElementLocated(
                                    By.xpath("$key//option[@label=\"${value}\"]")
                                )
                            ).click()
                    }
                    sendKeys(value)
                }
            }
        }
    }

    fun submitAndConfirm(driver: AppiumDriver<MobileElement>, original: By, alternative: By?) {
        // click on the submit button
        var submitButtonBy: By? = null

        try {
            DriverUtils.getWaiter(driver, 5).until(ExpectedConditions.presenceOfElementLocated(original)).click()
            submitButtonBy = original
        } catch (t: Throwable) {
            tryOptional {
                alternative?.let {
                    DriverUtils.getWaiter(driver, 5).until(ExpectedConditions.presenceOfElementLocated(it)).click()
                    submitButtonBy = it
                }
            }
        }

        if (submitButtonBy == null) {
            return
        }

        // check if we should continue anyway
        var confirmPresent = true
        var retries = 5
        do {
            try {
                retries--
                DriverUtils.wait(driver, 1)
                val continueAnyWay = driver.findElement(submitButtonBy)
                continueAnyWay.click()
            } catch (exception: Exception) {
                confirmPresent = false
            }
        } while (confirmPresent && retries > 0)
    }

    fun readConsoleMessage(driver: AppiumDriver<*>, containText: String): WebElement? {
        val by = if (driver is AndroidDriver) {
            By.xpath("//android.widget.TextView[contains(@text, '$containText')]")
        } else {
            By.xpath("//XCUIElementTypeOther[contains(@text, '$containText')]")
        }
        return DriverUtils.getWaiter(driver).until(ExpectedConditions.presenceOfElementLocated(by))
    }

    fun readStateMessage(driver: AppiumDriver<MobileElement>, paymentCategory: PaymentCategory): String? {
        val id = "state_${paymentCategory.value}"
        val by = ByRnId(driver, id)
        val stateLabel: WebElement = try {
            DriverUtils.getWaiter(driver).until(ExpectedConditions.presenceOfElementLocated(by))
        } catch (t: Throwable) {
            (driver as? AndroidDriver<*>)?.let { driver ->
                driver.findElementByAndroidUIAutomator("new UiScrollable(new UiSelector().scrollable(true).instance(0)).scrollIntoView(new UiSelector().description(\"$id\"))")
            }
            DriverUtils.getWaiter(driver).until(ExpectedConditions.presenceOfElementLocated(by))
        }
        return stateLabel.text
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
            when {
                json.has("approved") -> {
                    Assert.assertFalse(json.getBoolean("approved"))
                }
                json.has("error") -> {
                    val errorJson = json.getJSONObject("error")
                    Assert.assertEquals("Authorize", errorJson.getString("action"))
                }
                else -> {
                    Assert.assertTrue(false)
                }
            }
        }
    }

    fun fillCardInfo(driver: AppiumDriver<MobileElement>, is3ds: Boolean = false) {
        if (driver is AndroidDriver) {
            DriverUtils.getWaiter(driver).until(ExpectedConditions.presenceOfElementLocated(By.id("cardNumber")))
            driver.findElementById("cardNumber")
                .sendKeys(if (is3ds) AppiumTestConstants.CARD_NUMBER_3DS else AppiumTestConstants.CARD_NUMBER)
            driver.findElementById("expire").sendKeys(AppiumTestConstants.CARD_EXPIREDATE)
            driver.findElementById("securityCode").sendKeys(AppiumTestConstants.CARD_CVV)
        } else {
            DriverUtils.getWaiter(driver)
                .until(ExpectedConditions.presenceOfElementLocated(By.xpath("//XCUIElementTypeStaticText[@name='Card Number']")))
            driver.findElement(By.xpath("//XCUIElementTypeStaticText[@name='Card Number']")).apply {
                tapElementCenter(driver)
                sendKeys(if (is3ds) AppiumTestConstants.CARD_NUMBER_3DS else AppiumTestConstants.CARD_NUMBER)
            }
            driver.findElement(By.xpath("//XCUIElementTypeStaticText[@name='MM/YY']")).apply {
                tapElementCenter(driver)
                sendKeys(AppiumTestConstants.CARD_EXPIREDATE)
            }
            driver.findElement(By.xpath("//XCUIElementTypeStaticText[@name='CVC']")).apply {
                tapElementCenter(driver)
                sendKeys(AppiumTestConstants.CARD_CVV)
            }
        }
    }

    fun fillSmsCode(driver: AppiumDriver<MobileElement>) {
        // if there is authentication for sms code
        tryOptional {
            if (driver is AndroidDriver) {
                DriverUtils.getWaiter(driver)
                    .until(ExpectedConditions.presenceOfElementLocated(By.id("authentication-ui")))
                DriverUtils.getWaiter(driver)
                    .until(ExpectedConditions.presenceOfElementLocated(By.id("otp-intro-send-button")))
                    .click()
                DriverUtils.getWaiter(driver)
                    .until(ExpectedConditions.presenceOfElementLocated(By.xpath("//input[@type='tel']")))
                    .sendKeys("123456")
            } else {
                val otpButtonBy = By.xpath("//XCUIElementTypeButton[contains(@name,'code')]")
                DriverUtils.getWaiter(driver, 5).until(ExpectedConditions.presenceOfElementLocated(otpButtonBy)).click()
                val otpInputBy = By.xpath("//XCUIElementTypeTextField")
                DriverUtils.getWaiter(driver, 5).until(ExpectedConditions.presenceOfElementLocated(otpInputBy)).apply {
                    (this as MobileElement).longPressElementCenter(driver)
                    sendKeys("123456")
                    driver.hideKeyboardCompat()
                }
            }
            DriverUtils.wait(driver, 2)
        }
    }

    fun dismissConsole(driver: AppiumDriver<*>) {
        tryOptional {
            readConsoleMessage(driver, "Dismiss All")?.click()
        }
    }
}