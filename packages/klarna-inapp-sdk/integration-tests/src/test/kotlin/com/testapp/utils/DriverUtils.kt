package com.testapp.utils

import com.testapp.base.Platform
import io.appium.java_client.AppiumDriver
import io.appium.java_client.MobileElement
import io.appium.java_client.android.AndroidDriver
import io.appium.java_client.ios.IOSDriver
import io.appium.java_client.remote.AutomationName
import io.appium.java_client.service.local.AppiumDriverLocalService
import org.junit.jupiter.api.Assertions
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
    private const val WAIT_TIME = 30

    private val commonCapabilities: DesiredCapabilities
        get() {
            val desiredCapabilities = DesiredCapabilities()
            desiredCapabilities.setCapability("newCommandTimeout", 60)
            return desiredCapabilities
        }

    /**
     * Builds an PlatformDriver based on the local appium server
     *
     * @param appiumService Appium server to connect the driver to
     * @return PlatformDriver instance
     */
    fun getLocalDriver(appiumService: AppiumDriverLocalService, platform: Platform): AppiumDriver<MobileElement> {
        val desiredCapabilities = commonCapabilities
        when (platform) {
            Platform.ANDROID -> {
                desiredCapabilities.setCapability("chromedriverExecutable", "/Users/mahmoud.jafarinejad/Downloads/chromedriver") // TODO: Set accordingly if needed
                desiredCapabilities.setCapability("automationName", AutomationName.ANDROID_UIAUTOMATOR2)
                desiredCapabilities.setCapability("deviceName", "Android Emulator")
                desiredCapabilities.setCapability("platformName", platform.platformName)
                desiredCapabilities.setCapability("appPackage", "com.testapp")
                desiredCapabilities.setCapability("appActivity", "com.testapp.MainActivity")
                desiredCapabilities.setCapability("noReset", "true")
                return AndroidDriver(appiumService.url, desiredCapabilities)
            }

            Platform.IOS -> {
                desiredCapabilities.setCapability("automationName", AutomationName.IOS_XCUI_TEST)
                desiredCapabilities.setCapability("deviceName", "iPhone 12")
                desiredCapabilities.setCapability("platformName", platform.platformName)
                desiredCapabilities.setCapability("bundleId", "com.klarna.entp.dinhouse.inapp.sdk.react.native.testapp")
                desiredCapabilities.setCapability("appName", "TestApp")
                desiredCapabilities.setCapability("fullContextList", true)
                desiredCapabilities.setCapability("webviewConnectTimeout", 10000)
                return IOSDriver(appiumService.url, desiredCapabilities)
            }
        }
    }

    /**
     * Builds an PlatformDriver based on browser stack
     *
     * @param username browser stack username
     * @param password browser stack password
     * @return PlatformDriver instance
     */
    fun getBrowserstackDriver(
        username: String,
        password: String,
        testName: String,
        buildName: String,
        platform: Platform
    ): AppiumDriver<MobileElement> {
        val caps = commonCapabilities
//        caps.setCapability("browserstack.local", "true")
        caps.setCapability("browserstack.debug", "true")
        caps.setCapability("browserstack.networkLogs", "true")
        caps.setCapability("browserstack.appiumLogs", "true")
        caps.setCapability("browserstack.deviceLogs", "true")
        caps.setCapability("project", "IN-APP RN MOBILE SDK INTEGRATION")
        caps.setCapability("name", testName)

        caps.setCapability("build", "InApp-RN-SDK/${buildName}")

        when (platform) {
            Platform.ANDROID -> {
                caps.setCapability("app", "INAPP_RN_SDK_ANDROID_TEST_APP")
                caps.setCapability("automationName", AutomationName.ANDROID_UIAUTOMATOR2)
                caps.setCapability("platformName", platform.platformName)
                caps.setCapability("device", "Google Pixel 5")
                caps.setCapability("os_version", "11.0")
                caps.setCapability("recreateChromeDriverSessions", "true")

                return AndroidDriver(
                    URL("https://$username:$password@hub-cloud.browserstack.com/wd/hub"),
                    caps
                )
            }

            Platform.IOS -> {
                caps.setCapability("app", "INAPP_RN_SDK_IOS_TEST_APP")
                caps.setCapability("automationName", AutomationName.IOS_XCUI_TEST)
                caps.setCapability("device", "iPhone 12")
                caps.setCapability("os_version", "14")
                caps.setCapability("platformName", platform.platformName)
                caps.setCapability("bundleId", "com.klarna.entp.dinhouse.inapp.sdk.react.native.testapp")
                caps.setCapability("appName", "TestApp")
                caps.setCapability("fullContextList", true)
                caps.setCapability("nativeWebTap", true)
                caps.setCapability("webviewConnectTimeout", 10000)
                return IOSDriver(
                    URL("https://$username:$password@hub-cloud.browserstack.com/wd/hub"),
                    caps
                )
            }
        }
    }

    /**
     * Switches the context of driver to Native
     *
     * @param driver The driver we want its context to be switched to native
     */
    fun switchContextToNative(driver: AppiumDriver<MobileElement>) {
        if (!driver.context.startsWith(CONTEXT_NATIVE)) {
            driver.context(CONTEXT_NATIVE)
        }
    }

    /**
     * Switches the context of driver to web view
     *
     * @param driver The driver we want its context to be switched to web view
     */
    fun switchContextToWebView(driver: AppiumDriver<MobileElement>) {
        val currentContext = driver.context
        if (!currentContext.startsWith(CONTEXT_WEBVIEW)) {
            var availableContexts: Set<*> = driver.contextHandles
            var webViewAvailable = false
            var retries = 4
            val sleepTime = 500L
            do {
                try {
                    availableContexts = driver.contextHandles
                    if (driver is AndroidDriver) {
                        driver.context(CONTEXT_WEBVIEW + driver.capabilities.getCapability("appPackage"))
                        webViewAvailable = true
                    } else {
                        (availableContexts as? Set<Map<String, String>>)?.let { availableContexts ->
                            val contexts = availableContexts.filter {
                                it["title"].equals("Payment View")
                            }
                            val context = contexts.get(0)
                            driver.context(context["id"])
                            webViewAvailable = true
                        } ?: (availableContexts as? Set<String>)?.let { availableContexts ->
                            val context = availableContexts.first { it.startsWith(CONTEXT_WEBVIEW) }
                            driver.context(context)
                            webViewAvailable = true
                        }
                    }
                } catch (t: Throwable) {
                    retries--
                    Thread.sleep(sleepTime)
                }
            } while (!webViewAvailable && retries > 0)
            if (!webViewAvailable) {
                Assertions.fail<Any>("Couldn't switch to the web view context. Current context: $currentContext available contexts: ${availableContexts.joinToString()}")
            }
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
    fun waitForActivity(driver: AppiumDriver<MobileElement>, activity: String, timeOutInSeconds: Int) {
        if (driver is AndroidDriver<*>) {
            switchContextToNative(driver)
            var found = false
            val startTime = System.currentTimeMillis()
            while (System.currentTimeMillis() - startTime < timeOutInSeconds * 1000) {
                if (driver.currentActivity() == activity) {
                    found = true
                    break
                }
            }
            if (!found) {
                Assertions.fail<Any>("Couldn't find the activity: $activity")
            }
        }
    }

    /**
     * Switch the given driver to the given iframe
     *
     * @param driver The driver that we want it to switch
     * @param iframeName The iframe name we want to switch to
     */
    fun switchToIframe(driver: AppiumDriver<*>, iframeName: String) {
        getWaiter(driver).until(ExpectedConditions.frameToBeAvailableAndSwitchToIt(iframeName))
    }

    /**
     * Switch the given driver to the given iframe
     *
     * @param driver The driver that we want it to switch
     * @param iframeBy The iframe by locator we want to switch to
     */
    fun switchToIframe(driver: AppiumDriver<*>, iframeBy: By) {
        getWaiter(driver).until(ExpectedConditions.frameToBeAvailableAndSwitchToIt(iframeBy))
    }
}
