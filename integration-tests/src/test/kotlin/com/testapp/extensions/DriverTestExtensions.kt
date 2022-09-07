package com.testapp.extensions

import com.testapp.base.BaseAppiumTest
import io.appium.java_client.android.AndroidDriver
import org.openqa.selenium.JavascriptExecutor

/**
 * Sets the BrowserStack's test name
 *
 * @param name Name of the test
 */
internal fun AndroidDriver<*>.setTestName(name: String) {
    if (BaseAppiumTest.isLocalTest()) return
    val jse = this as JavascriptExecutor
    jse.executeScript("browserstack_executor: {\"action\": \"setSessionName\", \"arguments\": {\"name\":\"${name}\" }}")
}

/**
 * Sets the BrowserStack's test status
 *
 * @param success If the test is a success or not
 * @param reason Reason of success or failure
 */
internal fun AndroidDriver<*>.setTestStatus(success: Boolean, reason: String? = null) {
    if (BaseAppiumTest.isLocalTest()) return
    val statusReason = reason ?: ""
    val jse = this as JavascriptExecutor
    jse.executeScript("browserstack_executor: {\"action\": \"setSessionStatus\", \"arguments\": {\"status\": \"${if (success) "passed" else "failed"}\", \"reason\": \"${statusReason}\"}}")
}