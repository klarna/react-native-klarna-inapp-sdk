package com.testapp.extensions

import io.appium.java_client.AppiumDriver
import io.appium.java_client.MobileElement
import io.appium.java_client.android.AndroidElement
import io.appium.java_client.android.AndroidTouchAction
import io.appium.java_client.ios.IOSElement
import io.appium.java_client.ios.IOSTouchAction
import io.appium.java_client.touch.offset.PointOption

fun MobileElement.tapElementCenter(driver: AppiumDriver<*>) {
    val centerOfElement = this.center
    if (this is IOSElement) {
        IOSTouchAction(driver).tap(PointOption.point(centerOfElement.x, centerOfElement.y)).perform()
    } else if (this is AndroidElement) {
        AndroidTouchAction(driver).tap(PointOption.point(centerOfElement.x, centerOfElement.y)).perform()
    }
}

fun MobileElement.longPressElementCenter(driver: AppiumDriver<*>) {
    val centerOfElement = this.center
    if (this is IOSElement) {
        IOSTouchAction(driver).longPress(PointOption.point(centerOfElement.x, centerOfElement.y)).perform()
    } else if (this is AndroidElement) {
        AndroidTouchAction(driver).longPress(PointOption.point(centerOfElement.x, centerOfElement.y)).perform()
    }
}