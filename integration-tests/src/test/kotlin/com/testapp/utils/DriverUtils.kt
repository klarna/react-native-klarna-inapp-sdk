package com.testapp.utils

import io.appium.java_client.AppiumDriver
import io.appium.java_client.android.AndroidDriver
import io.appium.java_client.remote.AutomationName
import io.appium.java_client.service.local.AppiumDriverLocalService
import org.junit.Assert.fail
import org.openqa.selenium.By
import org.openqa.selenium.TimeoutException
import org.openqa.selenium.WebElement
import org.openqa.selenium.remote.DesiredCapabilities
import org.openqa.selenium.support.ui.ExpectedConditions
import org.openqa.selenium.support.ui.WebDriverWait
import java.net.URL

/**
 * Helper class containing common functions regarding Appium driver
 */
internal object DriverUtils {
    private const val CONTEXT_NATIVE = "NATIVE_APP"
    private const val CONTEXT_WEBVIEW = "WEBVIEW_"
    private const val WAIT_TIME = 15

    private val commonCapabilities: DesiredCapabilities
        get() {
            val desiredCapabilities = DesiredCapabilities()
            desiredCapabilities.setCapability("newCommandTimeout", 60)
            desiredCapabilities.setCapability("automationName", AutomationName.ANDROID_UIAUTOMATOR2)
            desiredCapabilities.setCapability("deviceName", "Android Emulator")
            desiredCapabilities.setCapability("platformName", "Android")
            return desiredCapabilities
        }

    /**
     * Builds an AndroidDriver based on the local appium server
     *
     * @param appiumService Appium server to connect the driver to
     * @return AndroidDriver instance
     */
    fun getLocalDriver(appiumService: AppiumDriverLocalService): AndroidDriver<WebElement> {
        val desiredCapabilities = commonCapabilities
        //desiredCapabilities.setCapability("chromedriverExecutable", "/Users/mahmoud.jafarinejad/Downloads/chromedriver") // TODO: Set accordingly if needed
        desiredCapabilities.setCapability("appPackage", "com.testapp")
        desiredCapabilities.setCapability("appActivity", "com.testapp.MainActivity")
        desiredCapabilities.setCapability("noReset", "true")
        return AndroidDriver(appiumService.url, desiredCapabilities)
    }

    /**
     * Builds an AndroidDriver based on browser stack
     *
     * @param username browser stack username
     * @param password browser stack password
     * @return AndroidDriver instance
     */
    fun getBrowserstackDriver(
            username: String,
            password: String,
            testName: String?
    ): AndroidDriver<WebElement> {
        val app = "INAPP_SDK_TEST_APP"
        val caps = commonCapabilities
        caps.setCapability("browserstack.local", "true")
        caps.setCapability("browserstack.debug", "true")
        caps.setCapability("browserstack.networkLogs", "true")
        caps.setCapability("browserstack.appiumLogs", "true")
        caps.setCapability("browserstack.deviceLogs", "true")
        caps.setCapability("recreateChromeDriverSessions", "true")
        caps.setCapability("device", "Samsung Galaxy S9")
        caps.setCapability("app", app)
        caps.setCapability("project", "IN-APP RN MOBILE SDK INTEGRATION")
        caps.setCapability("name", testName)
        return AndroidDriver(
                URL("https://$username:$password@hub-cloud.browserstack.com/wd/hub"),
                caps
        )
    }

    /**
     * Switches the context of driver to Native
     *
     * @param driver The driver we want its context to be switched to native
     */
    fun switchContextToNative(driver: AppiumDriver<*>) {
        driver.context(CONTEXT_NATIVE)
    }

    /**
     * Switches the context of driver to web view
     *
     * @param driver The driver we want its context to be switched to web view
     */
    fun switchContextToWebView(driver: AppiumDriver<*>) {
        var webViewAvailable = false
        var retries = 4
        val sleepTime = 500L
        do {
            try {
                driver.context(CONTEXT_WEBVIEW + driver.capabilities.getCapability("appPackage"))
                webViewAvailable = true
            } catch (t: Throwable){
                retries--
                Thread.sleep(sleepTime)
            }
        } while (!webViewAvailable && retries > 0)
        if(!webViewAvailable){
            fail("Couldn't switch to the web view context.")
        }
    }

    /**
     * Makes the given driver to wait and do nothing for a given amount of time
     *
     * @param driver        The driver that we want it to wait
     * @param waitTimeInSec The amount of time (in seconds) we want the driver to wait
     */
    fun wait(driver: AppiumDriver<*>?, waitTimeInSec: Int) {
        val wait = WebDriverWait(driver, waitTimeInSec.toLong(), (waitTimeInSec * 1000).toLong())
        try {
            wait.until<Boolean> { false }
        } catch (exception: TimeoutException) { // it was expected.
        }
    }

    /**
     * Gives the associated waiter for the given driver
     *
     * @param driver The driver that we want its waiter
     * @return WebDriverWait object for the given driver
     */
    fun getWaiter(driver: AppiumDriver<*>?): WebDriverWait {
        return getWaiter(driver, WAIT_TIME)
    }

    /**
     * Gives the associated waiter for the given driver
     *
     * @param driver The driver that we want its waiter
     * @param timeOutInSeconds The timeout value for the waiter in seconds
     * @return WebDriverWait object for the given driver
     */
    fun getWaiter(driver: AppiumDriver<*>?, timeOutInSeconds: Int): WebDriverWait {
        return WebDriverWait(driver, timeOutInSeconds.toLong(), 500)
    }

    /**
     * Waits for an element to be present in the context
     *
     * @param driver The driver that we want it to wait
     * @param timeOutInSeconds The timeout value for the waiter in seconds
     * @return The WebElement object found present
     */
    fun waitForPresence(driver: AppiumDriver<*>?, by: By, timeOutInSeconds: Int = WAIT_TIME): WebElement {
        return getWaiter(driver, timeOutInSeconds).until(ExpectedConditions.presenceOfElementLocated(by))
    }

    /**
     * Waits for an activity to open
     *
     * @param driver The driver that we want it to wait
     * @param activity The activity we want to wait for to load
     */
    fun waitForActivity(driver: AndroidDriver<*>, activity: String, timeOutInSeconds: Int){
        switchContextToNative(driver)
        var found = false
        val startTime = System.currentTimeMillis()
        while(System.currentTimeMillis() - startTime < timeOutInSeconds * 1000){
            if (driver.currentActivity() == activity){
                found = true
                break
            }
        }
        if(!found){
            fail("Couldn't find the activity: $activity")
        }
    }

    fun isAndroid(driver: AppiumDriver<*>) = driver.capabilities.getCapability("platformName") == "Android"
    fun isIos(driver: AppiumDriver<*>) = driver.capabilities.getCapability("platformName") == "ios"
}