package com.testapp.extensions

import com.testapp.utils.DriverUtils
import io.appium.java_client.AppiumDriver
import io.appium.java_client.ios.IOSDriver
import io.appium.java_client.remote.HideKeyboardStrategy
import org.openqa.selenium.By

fun AppiumDriver<*>.hideKeyboardCompat() {
    try {
        hideKeyboard()
        DriverUtils.wait(this, 1)
        if (this is IOSDriver) {
            if (isKeyboardShown()) {
                hideKeyboard(HideKeyboardStrategy.PRESS_KEY, "Done")
                DriverUtils.wait(this, 1)
                if (isKeyboardShown()) {
                    hideKeyboard(HideKeyboardStrategy.TAP_OUTSIDE)
                }
                DriverUtils.wait(this, 1)
                if (isKeyboardShown()) {
                    findElement(By.xpath("//XCUIElementTypeButton[@name='Done']")).click()
                }
            }
        }
    } catch (e: Throwable) {

    }
}