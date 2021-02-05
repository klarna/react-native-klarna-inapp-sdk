package com.testapp.base

import com.testapp.extensions.hideKeyboardCompat
import com.testapp.extensions.removeWhitespace
import com.testapp.utils.ByRnId
import com.testapp.utils.DriverUtils
import com.testapp.utils.DriverUtils.getBrowserstackDriver
import com.testapp.utils.DriverUtils.getLocalDriver
import io.appium.java_client.AppiumDriver
import io.appium.java_client.MobileElement
import io.appium.java_client.service.local.AppiumDriverLocalService
import io.appium.java_client.service.local.AppiumServiceBuilder
import io.appium.java_client.service.local.flags.GeneralServerFlag
import org.junit.*
import org.junit.rules.TestName
import org.openqa.selenium.support.ui.ExpectedConditions

internal open class BaseAppiumTest {

    protected val retryCount = 2
    protected val ignoreOnFailure = false
    internal lateinit var driver: AppiumDriver<MobileElement>
    private var appiumService: AppiumDriverLocalService? = null

    val platform = Platform.getSystemConfiguration()

    @Rule
    @JvmField
    var retryRule = RetryRule(retryCount, ignoreOnFailure)

    @Rule
    @JvmField
    var name: TestName = TestName()

    @Before
    fun setup() {
        val localAppiumTest: Boolean

        val browserstackUsername = System.getProperty("browserstack.username")
        val browserstackPassword = System.getProperty("browserstack.password")
        val buildName = System.getProperty("build.name")

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
                    "${this.javaClass.simpleName} - ${name.methodName}",
                    buildName,
                    platform
                )
            } catch (t: Throwable) {
                Assume.assumeNoException(t)
            }
        }
    }

    @After
    fun quit() {
        driver.quit()
    }

    fun android() = platform == Platform.ANDROID

    fun ios() = platform == Platform.IOS

    fun initLoadSDK(token: String?, category: String) {
        DriverUtils.getWaiter(driver)
            .until(ExpectedConditions.presenceOfElementLocated(ByRnId(driver, "setTokenInput"))).apply {
                val trimmedToken = token?.removeWhitespace()
                sendKeys(trimmedToken)
                Assert.assertEquals(trimmedToken, text?.removeWhitespace())
            }
        driver.hideKeyboardCompat()
        val initButton = DriverUtils.getWaiter(driver)
            .until(ExpectedConditions.elementToBeClickable(ByRnId(driver, "initButton_${category}")))
        initButton.click()
        DriverUtils.wait(driver, 1)
        initButton.click()
        //wait for init response
        DriverUtils.wait(driver, 1)
        driver.findElement(ByRnId(driver, "loadButton_${category}")).click()
        DriverUtils.wait(driver, 1)
        driver.findElement(ByRnId(driver, "loadButton_${category}")).click()
    }
}