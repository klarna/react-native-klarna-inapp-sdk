package com.testapp.base

import com.testapp.utils.DriverUtils
import com.testapp.utils.DriverUtils.getBrowserstackDriver
import com.testapp.utils.DriverUtils.getLocalDriver
import io.appium.java_client.service.local.AppiumDriverLocalService
import io.appium.java_client.service.local.AppiumServiceBuilder
import io.appium.java_client.service.local.flags.GeneralServerFlag
import org.junit.*
import org.openqa.selenium.WebElement

internal open class BaseAppiumTest {

    protected val retryCount = 2
    protected val ignoreOnFailure = false

    companion object {

        private var platform = Platform.ANDROID
        internal lateinit var driver: PlatformDriver<WebElement>
        private var appiumService: AppiumDriverLocalService? = null
        internal var testName: String? = null

        @JvmStatic
        @BeforeClass
        fun setup() {
            val localAppiumTest: Boolean

            val browserstackUsername = System.getProperty("browserstack.username")
            val browserstackPassword = System.getProperty("browserstack.password")
            val inputPlatform = System.getProperty("platform")

            platform = when(inputPlatform.toLowerCase()){
                "android" -> Platform.ANDROID
                "ios" -> Platform.IOS
                else -> Platform.ANDROID
            }

            localAppiumTest = browserstackUsername == null || browserstackPassword == null
            if (localAppiumTest) {
                if (appiumService == null) {
                    synchronized(AppiumDriverLocalService::class.java) {
                        if (appiumService == null) {
                            appiumService =
                                    AppiumServiceBuilder().withArgument(GeneralServerFlag.LOG_LEVEL, "error").build()
                                            .apply {
                                                start()
                                            }
                        }
                    }
                }
                driver = getLocalDriver(appiumService!!, platform)
            } else {
                try {
                    driver = getBrowserstackDriver(
                            browserstackUsername,
                            browserstackPassword,
                            testName,
                            platform
                    )
                } catch (t: Throwable) {
                    Assume.assumeNoException(t)
                }
            }
        }

        @JvmStatic
        @AfterClass
        fun quit() {
            driver.quit()
        }
    }

    @Before
    fun reset() {
        DriverUtils.switchContextToNative(driver)
        driver.launchApp()
        DriverUtils.switchContextToNative(driver)
    }
}