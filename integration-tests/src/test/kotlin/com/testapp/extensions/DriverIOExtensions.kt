package com.testapp.extensions

import io.appium.java_client.AppiumDriver
import io.appium.java_client.android.AndroidDriver
import io.appium.java_client.ios.IOSDriver
import io.appium.java_client.remote.HideKeyboardStrategy

fun AppiumDriver<*>.hideKeyboardCompat() {
    try {
        if (this is AndroidDriver) {
            hideKeyboard()
        } else if (this is IOSDriver) {
            try {
                hideKeyboard(HideKeyboardStrategy.PRESS_KEY, "Done")
            } catch (e: Throwable) {
                hideKeyboard()
            }
        }
    } catch (e: Throwable) {

    }
}