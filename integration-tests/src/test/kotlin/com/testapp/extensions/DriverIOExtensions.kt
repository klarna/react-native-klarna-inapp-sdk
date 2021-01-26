package com.testapp.extensions

import com.testapp.base.BaseAppiumTest
import com.testapp.utils.DriverUtils
import io.appium.java_client.AppiumDriver
import io.appium.java_client.HasOnScreenKeyboard
import io.appium.java_client.MobileElement
import io.appium.java_client.android.AndroidDriver
import io.appium.java_client.ios.IOSDriver
import io.appium.java_client.ios.IOSElement
import io.appium.java_client.ios.IOSTouchAction
import io.appium.java_client.remote.HideKeyboardStrategy
import io.appium.java_client.touch.offset.PointOption
import org.openqa.selenium.By
import org.openqa.selenium.Keys
import org.openqa.selenium.WebElement

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

fun AppiumDriver<*>.isKeyboardVisible(): Boolean {
    return if (this is HasOnScreenKeyboard) isKeyboardShown else false
}

fun AppiumDriver<*>.selectAll(element: WebElement) {
    if (element is IOSElement) {
        element.longPressElementCenter(this)
        try {
            findElement(By.xpath("//XCUIElementTypeMenuItem[@name=\"Select All\"]")).click()
        } catch (t: Throwable) {

        }
    } else {
        element.sendKeys(Keys.chord(Keys.CONTROL, "a"))
    }
}

fun AppiumDriver<*>.deleteAll(element: WebElement) {
    selectAll(element)
    try {
        element.sendKeys(Keys.DELETE)
    } catch (t: Throwable) {

    }
}
