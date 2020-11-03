package com.testapp.base

import com.testapp.utils.ByRnId
import com.testapp.utils.DriverUtils
import com.testapp.utils.DriverUtils.getBrowserstackDriver
import com.testapp.utils.DriverUtils.getLocalDriver
import io.appium.java_client.AppiumDriver
import io.appium.java_client.MobileElement
import io.appium.java_client.android.AndroidDriver
import io.appium.java_client.service.local.AppiumDriverLocalService
import io.appium.java_client.service.local.AppiumServiceBuilder
import io.appium.java_client.service.local.flags.GeneralServerFlag
import org.junit.*
import org.openqa.selenium.WebElement
import org.openqa.selenium.support.ui.ExpectedConditions

internal open class BaseAppiumTest {

    protected val retryCount = 2
    protected val ignoreOnFailure = false

    companion object {

        internal lateinit var driver: AndroidDriver<MobileElement>
        private var appiumService: AppiumDriverLocalService? = null
        internal var testName: String? = null

        @JvmStatic
        @BeforeClass
        fun setup() {
            val localAppiumTest: Boolean

            val browserstackUsername = System.getProperty("browserstack.username")
            val browserstackPassword = System.getProperty("browserstack.password")

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
                driver = getLocalDriver(appiumService!!)
            } else {
                try {
                    driver = getBrowserstackDriver(
                            browserstackUsername,
                            browserstackPassword,
                            testName
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

    fun initLoadSDK(token: String?, category: String){
        DriverUtils.getWaiter(driver).until(ExpectedConditions.presenceOfElementLocated(ByRnId(driver, "setTokenInput"))).apply {
            sendKeys(token)
            Assert.assertEquals(token, text)
        }
        DriverUtils.getWaiter(BaseAppiumTest.driver).until(ExpectedConditions.elementToBeClickable(ByRnId(driver,"initButton_${category}"))).click()
        //wait for init response
        DriverUtils.wait(BaseAppiumTest.driver, 1)
        BaseAppiumTest.driver.findElement(ByRnId(driver,"loadButton_${category}")).click()
        DriverUtils.wait(BaseAppiumTest.driver, 1)
        BaseAppiumTest.driver.findElement(ByRnId(driver,"loadButton_${category}")).click()
    }
}