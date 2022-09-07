package com.testapp.base

import com.testapp.extensions.hideKeyboardCompat
import com.testapp.extensions.removeWhitespace
import com.testapp.extensions.tryOptional
import com.testapp.utils.ByRnId
import com.testapp.utils.DriverUtils
import com.testapp.utils.DriverUtils.getBrowserstackDriver
import com.testapp.utils.DriverUtils.getLocalDriver
import com.testapp.utils.PaymentFlowsTestHelper
import com.testapp.utils.ServiceUtil
import io.appium.java_client.AppiumDriver
import io.appium.java_client.MobileElement
import io.appium.java_client.android.AndroidDriver
import io.appium.java_client.service.local.AppiumDriverLocalService
import io.appium.java_client.service.local.AppiumServiceBuilder
import io.appium.java_client.service.local.flags.GeneralServerFlag
import org.junit.*
import org.junit.jupiter.api.Assertions
import org.openqa.selenium.support.ui.ExpectedConditions

internal abstract class BaseAppiumTest {

    companion object {

        val platform = Platform.getSystemConfiguration()

        private fun getBrowserstackCredentials(): Pair<String, String> {
            val browserstackUsername = System.getProperty("browserstack.username")
            val browserstackPassword = System.getProperty("browserstack.password")
            return Pair(browserstackUsername, browserstackPassword)
        }

        private fun getBuildName(): String? {
            return System.getProperty("build.name")
        }

        fun isLocalTest(): Boolean {
            val browserstackCredentials = getBrowserstackCredentials()
            return browserstackCredentials.first.isNullOrEmpty() || browserstackCredentials.second.isNullOrEmpty()
        }

        fun setupDriver(testName: String?): AppiumDriver<MobileElement> {
            return if (isLocalTest()) {
                val appiumService = ServiceUtil.getAppiumService()
                DriverUtils.getLocalDriver(appiumService, platform)
            } else {
                val browserstackCredentials = getBrowserstackCredentials()
                DriverUtils.getBrowserstackDriver(
                    browserstackCredentials.first,
                    browserstackCredentials.second,
                    testName,
                    getBuildName(),
                    platform
                )
            }
        }
    }

    fun android() = platform == Platform.ANDROID

    fun ios() = platform == Platform.IOS
}
