package com.testapp.extensions

import io.appium.java_client.MobileElement
import org.openqa.selenium.Point
import org.openqa.selenium.WebElement

val WebElement.center: Point
    get() {
        return if (this is MobileElement) {
            center
        } else {
            val start = location
            val dimensions = size
            Point(start.x + (dimensions.width / 2), start.y + (dimensions.height / 2))
        }
    }