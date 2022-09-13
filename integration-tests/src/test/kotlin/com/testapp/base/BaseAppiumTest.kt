package com.testapp.base

import com.testapp.utils.DriverUtils
import com.testapp.utils.ScriptHelper
import com.testapp.utils.ServiceUtil
import io.appium.java_client.AppiumDriver
import io.appium.java_client.MobileElement

internal abstract class BaseAppiumTest {

    companion object {

        val platform = Platform.getSystemConfiguration()

        private fun getBrowserstackCredentials(): Pair<String?, String?> {
            val browserstackUsername = System.getProperty("browserstack.username")
            val browserstackPassword = System.getProperty("browserstack.password")
            return Pair(browserstackUsername, browserstackPassword)
        }

        private fun getBuildName(): String {
            val buildName = System.getProperty("build.name")
            return if (buildName.isNullOrEmpty()) {
                "${ScriptHelper.getGitBranch()}@${ScriptHelper.getGitCommit()}"
            } else {
                buildName
            }
        }

        fun isLocalTest(): Boolean {
            val browserstackCredentials = getBrowserstackCredentials()
            return browserstackCredentials.first.isNullOrEmpty() || browserstackCredentials.second.isNullOrEmpty()
        }

        fun setupDriver(testName: String): AppiumDriver<MobileElement> {
            return if (isLocalTest()) {
                val appiumService = ServiceUtil.getAppiumService()
                DriverUtils.getLocalDriver(appiumService, platform)
            } else {
                val browserstackCredentials = getBrowserstackCredentials()
                DriverUtils.getBrowserstackDriver(
                    browserstackCredentials.first!!,
                    browserstackCredentials.second!!,
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
