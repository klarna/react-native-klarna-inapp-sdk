package com.testapp.utils

import com.testapp.base.BaseAppiumTest
import com.testapp.constants.AppiumTestConstants
import io.appium.java_client.MobileElement
import io.appium.java_client.android.AndroidDriver
import io.appium.java_client.android.nativekey.AndroidKey
import io.appium.java_client.android.nativekey.KeyEvent
import org.json.JSONObject
import org.junit.Assert
import org.openqa.selenium.*
import org.openqa.selenium.support.ui.ExpectedConditions

internal object PaymentFlowsTestHelper {

    fun fillBillingAddress(driver: AndroidDriver<MobileElement>, billingInfo: LinkedHashMap<String, String?>) {
        val loadingLocator = By.id("loading-overlay")

        // switch to klarna payment billing address iframe
        val billingWindow =
                WebViewTestHelper.findWindowFor(driver, By.id("klarna-some-hardcoded-instance-id-fullscreen"))
        billingWindow?.let {
            driver.switchTo().window(it)
        } ?: Assert.fail("Billing address window wasn't found")
        DriverUtils.getWaiter(driver)
                .until(ExpectedConditions.frameToBeAvailableAndSwitchToIt("klarna-some-hardcoded-instance-id-fullscreen"))

        val selectAllKeys = Keys.chord(Keys.CONTROL, "a")
        for ((key, value) in billingInfo) {
            value?.let {
                try {
                    val element =
                            DriverUtils.getWaiter(driver, 5).until(ExpectedConditions.presenceOfElementLocated(By.xpath(key)))
                    element.apply {
                        if (isEnabled) {
                            sendKeys(selectAllKeys)
                            sendKeys(Keys.DELETE)
                            DriverUtils.getWaiter(driver)
                                    .until(ExpectedConditions.invisibilityOfElementLocated(loadingLocator))
                            sendKeys(value)
                        }
                    }
                    driver.pressKey(KeyEvent(AndroidKey.ENTER))
                } catch (t: TimeoutException) {
                    // element is not visible (not required to fill)
                } catch (t: StaleElementReferenceException) {
                    // element is not attached
                }
            }
        }

        // click on the submit button
        var submitButtonBy: By? = null
        val submitButtonByDefault = By.id("identification-dialog__footer-button-wrapper")
        val submitButtonByInParts = By.id("payinparts_kp-address-collection-dialog__footer-button-wrapper")

        try {
            driver.findElement(submitButtonByDefault).click()
            submitButtonBy = submitButtonByDefault
        } catch (t: Throwable) {
            driver.findElement(submitButtonByInParts).click()
            submitButtonBy = submitButtonByInParts
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

    fun readConsoleMessage(driver: AndroidDriver<MobileElement>, containText: String): WebElement?{
        return DriverUtils.getWaiter(driver).until(ExpectedConditions.presenceOfElementLocated(By.xpath("//android.widget.TextView[contains(@text, '$containText')]")))
    }

    fun checkAuthorizeResponse(response: String?, successful: Boolean) {
        assert(!response.isNullOrBlank())
        val json = JSONObject(response!!.substring(response.indexOf("{")))
        if(successful){
            assert(json.getBoolean("approved"))
            try{
                json.getString("authToken").toLowerCase().apply {
                    assert(!this.isBlank() && this != "null")
                }
            } catch (exception: Exception){
                Assert.fail("Null authorization token.")
            }
        }
        else {
            Assert.assertFalse(json.getBoolean("approved"))
        }
    }

    fun fillCardInfo(driver: AndroidDriver<MobileElement>, is3ds: Boolean = false) {
        DriverUtils.getWaiter(driver).until(ExpectedConditions.presenceOfElementLocated(By.id("cardNumber")))
        driver.findElementById("cardNumber").sendKeys(if(is3ds) AppiumTestConstants.CARD_NUMBER_3DS else AppiumTestConstants.CARD_NUMBER)
        driver.findElementById("expire").sendKeys(AppiumTestConstants.CARD_EXPIREDATE)
        driver.findElementById("securityCode").sendKeys(AppiumTestConstants.CARD_CVV)
    }

    fun dismissConsole() {
        try {
            readConsoleMessage(BaseAppiumTest.driver, "Dismiss All")?.click()
        } catch (t: Throwable){}
    }
}