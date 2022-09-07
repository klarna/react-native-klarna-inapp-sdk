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
import org.junit.jupiter.api.Assertions
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
            } ?: Assertions.fail("Billing address window wasn't found")
            DriverUtils.switchToIframe(driver, "klarna-some-hardcoded-instance-id-fullscreen")
        } else {
            tryOptional {
                DriverUtils.waitForPresence(
                    driver,
                    By.xpath("//XCUIElementTypeOther[contains(@name,'Please enter your')]")
                )
            } ?: tryOptional {
                DriverUtils.waitForPresence(
                    driver,
                    By.xpath("//XCUIElementTypeOther[contains(@name,'Verify your details')]")
                )
            }
        }

        fillInfoAll(driver, billingInfo)
        submitAndConfirm(driver)

        tryOptional {
            fillInfo(driver, billingInfo, billingInfo.options.idNumber)
            submitAndConfirm(driver)
        }

        tryOptional {
            fillInfo(driver, billingInfo, billingInfo.options.birthday)
            submitAndConfirm(driver)
        }
    }

    private fun fillInfoAll(driver: AppiumDriver<MobileElement>, billingInfo: BillingInfo) {
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
    }

    private fun fillInfo(driver: AppiumDriver<MobileElement>, billingInfo: BillingInfo, value: String?) {
        tryOptional {
            value?.let {
                billingInfo.linkedMap().filterValues { it == value }.forEach { (fieldKey, fieldValue) ->
                    try {
                        fillInfo(driver, billingInfo, fieldKey, fieldValue)
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
        }
    }

    private fun fillInfo(driver: AppiumDriver<MobileElement>, billingInfo: BillingInfo, key: String, value: String?) {
        val element = DriverUtils.waitForPresence(driver, By.xpath(key), 5)
        element.apply {
            if (isEnabled || driver is IOSDriver) {
                DriverUtils.getWaiter(driver)
                    .until(ExpectedConditions.invisibilityOfElementLocated(By.id("loading-overlay")))
                if (this is IOSElement) {
                    tapElementCenter(driver)
                    DriverUtils.wait(driver, 5)

                    if (billingInfo.identifiers.title == key) {
                        DriverUtils.waitForPresence(driver, By.xpath("//XCUIElementTypePickerWheel")).sendKeys(value)
                        tryOptional {
                            DriverUtils.waitForPresence(driver, By.xpath("//XCUIElementTypeButton[@name='Done']"))
                                .click()
                        }
                    }
                    if (driver.isKeyboardVisible()) {
                        sendKeys(value)
                    }
                } else {
                    if (billingInfo.identifiers.title == key) {
                        DriverUtils.waitForPresence(driver, By.xpath("$key//option[@label=\"${value}\"]")).click()
                    }
                    sendKeys(value)
                }
            }
        }
    }

    private fun submitAndConfirm(driver: AppiumDriver<MobileElement>) {
        tryOptional {
            if (driver is AndroidDriver) {
                submitAndConfirm(
                    driver,
                    By.id("identification-dialog__footer-button-wrapper"),
                    By.id("payinparts_kp-address-collection-dialog__footer-button-wrapper"),
                    By.id("invoice_kp-purchase-approval__footer-button-wrapper"),
                    By.id("btn-continue")
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
    }

    private fun submitAndConfirm(driver: AppiumDriver<MobileElement>, vararg submitLocators: By) {
        // click on the submit button
        var submitButtonBy: By? = null

        for (submit in submitLocators) {
            try {
                DriverUtils.waitForPresence(driver, submit, 5).click()
                submitButtonBy = submit
                break
            } catch (t: Throwable) {
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
        return DriverUtils.waitForPresence(driver, by)
    }

    fun readStateMessage(driver: AppiumDriver<MobileElement>, paymentCategory: PaymentCategory): String? {
        val id = "state_${paymentCategory.value}"
        val by = ByRnId(driver, id)
        val stateLabel: WebElement = try {
            DriverUtils.waitForPresence(driver, by)
        } catch (t: Throwable) {
            (driver as? AndroidDriver<*>)?.let { driver ->
                driver.findElementByAndroidUIAutomator("new UiScrollable(new UiSelector().scrollable(true).instance(0)).scrollIntoView(new UiSelector().description(\"$id\"))")
            }
            DriverUtils.waitForPresence(driver, by)
        }
        return stateLabel.text
    }

    fun waitStateMessage(
        driver: AppiumDriver<MobileElement>,
        paymentCategory: PaymentCategory,
        containText: String
    ): String? {
        val id = "state_${paymentCategory.value}"
        val by = if (driver is IOSDriver) {
            By.xpath("//*[@name='$id' and contains(@label, '$containText')]")
        } else {
            By.xpath("//*[@content-desc='$id' and contains(@text, '$containText')]")
        }
        val stateLabel: WebElement = try {
            DriverUtils.waitForPresence(driver, by)
        } catch (t: Throwable) {
            (driver as? AndroidDriver<*>)?.let { driver ->
                driver.findElementByAndroidUIAutomator("new UiScrollable(new UiSelector().scrollable(true).instance(0)).scrollIntoView(new UiSelector().description(\"$id\").textContains(\"$containText\"))")
            }
            DriverUtils.waitForPresence(driver, by)
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
                Assertions.fail<Any>("Null authorization token.")
            }
        } else {
            when {
                json.has("approved") -> {
                    Assertions.assertFalse(json.getBoolean("approved"))
                }

                json.has("error") -> {
                    val errorJson = json.getJSONObject("error")
                    Assertions.assertEquals("Authorize", errorJson.getString("action"))
                }

                else -> {
                    Assertions.assertTrue(false)
                }
            }
        }
    }

    fun fillCardInfo(driver: AppiumDriver<MobileElement>, is3ds: Boolean = false) {
        if (driver is AndroidDriver) {
            DriverUtils.waitForPresence(driver, By.id("cardNumber"))
            driver.findElementById("cardNumber")
                .sendKeys(if (is3ds) AppiumTestConstants.CARD_NUMBER_3DS else AppiumTestConstants.CARD_NUMBER)
            driver.findElementById("expire").sendKeys(AppiumTestConstants.CARD_EXPIREDATE)
            driver.findElementById("securityCode").sendKeys(AppiumTestConstants.CARD_CVV)
        } else {
            DriverUtils.waitForPresence(driver, By.xpath("//XCUIElementTypeStaticText[@name='Card Number']"))
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
                DriverUtils.waitForPresence(driver, By.id("authentication-ui"))
                DriverUtils.waitForPresence(driver, By.id("otp-intro-send-button"))
                DriverUtils.waitForPresence(driver, By.xpath("//input[@type='tel']")).sendKeys("123456")
            } else {
                val otpButtonBy = By.xpath("//XCUIElementTypeButton[contains(@name,'code')]")
                DriverUtils.waitForPresence(driver, otpButtonBy, 5).click()
                val otpInputBy = By.xpath("//XCUIElementTypeTextField")
                DriverUtils.waitForPresence(driver, otpInputBy, 5).apply {
                    longPressElementCenter(driver)
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
