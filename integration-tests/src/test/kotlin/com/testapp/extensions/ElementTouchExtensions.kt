package com.testapp.extensions

import com.testapp.base.BaseAppiumTest
import io.appium.java_client.MobileElement
import io.appium.java_client.android.AndroidElement
import io.appium.java_client.android.AndroidTouchAction
import io.appium.java_client.ios.IOSElement
import io.appium.java_client.ios.IOSTouchAction
import io.appium.java_client.touch.offset.PointOption

fun MobileElement.tapElementCenter() {
    val centerOfElement = this.center
    if (this is IOSElement) {
        IOSTouchAction(BaseAppiumTest.driver).tap(PointOption.point(centerOfElement.x, centerOfElement.y)).perform()
    } else if (this is AndroidElement) {
        AndroidTouchAction(BaseAppiumTest.driver).tap(PointOption.point(centerOfElement.x, centerOfElement.y)).perform()
    }
}