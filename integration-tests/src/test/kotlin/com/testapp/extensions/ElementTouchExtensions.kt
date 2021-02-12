package com.testapp.extensions

import io.appium.java_client.AppiumDriver
import io.appium.java_client.android.AndroidElement
import io.appium.java_client.android.AndroidTouchAction
import io.appium.java_client.ios.IOSElement
import io.appium.java_client.ios.IOSTouchAction
import io.appium.java_client.touch.offset.PointOption
import org.openqa.selenium.WebElement

fun WebElement.tapElementCenter(driver: AppiumDriver<*>) {
    val centerOfElement = this.center
    if (this is IOSElement) {
        IOSTouchAction(driver).tap(PointOption.point(centerOfElement.x, centerOfElement.y)).perform()
    } else if (this is AndroidElement) {
        AndroidTouchAction(driver).tap(PointOption.point(centerOfElement.x, centerOfElement.y)).perform()
    }
}

fun WebElement.longPressElementCenter(driver: AppiumDriver<*>) {
    val centerOfElement = this.center
    if (this is IOSElement) {
        IOSTouchAction(driver).longPress(PointOption.point(centerOfElement.x, centerOfElement.y)).perform()
    } else if (this is AndroidElement) {
        AndroidTouchAction(driver).longPress(PointOption.point(centerOfElement.x, centerOfElement.y)).perform()
    }
}