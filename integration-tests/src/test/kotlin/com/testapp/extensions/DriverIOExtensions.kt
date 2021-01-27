package com.testapp.extensions

import com.testapp.utils.DriverUtils
import io.appium.java_client.AppiumDriver
import io.appium.java_client.HasOnScreenKeyboard
import io.appium.java_client.ios.IOSDriver
import io.appium.java_client.ios.IOSElement
import io.appium.java_client.remote.HideKeyboardStrategy
import org.openqa.selenium.By
import org.openqa.selenium.Keys
import org.openqa.selenium.WebElement

fun AppiumDriver<*>.hideKeyboardCompat() {
    tryOptional {
        tryOptional {
            hideKeyboard()
            DriverUtils.wait(this, 1)
        }
        if (this is IOSDriver) {
            tryOptional {
                if (isKeyboardVisible()) {
                    findElement(By.xpath("//XCUIElementTypeButton[@name='Done']")).click()
                }
                DriverUtils.wait(this, 1)
            }
            tryOptional {
                if (isKeyboardVisible()){
                    findElement(By.xpath("//XCUIElementTypeKeyboard//XCUIElementTypeButton[@name='Return']")).click()
                }
                DriverUtils.wait(this, 1)
            }
            tryOptional {
                if (isKeyboardVisible()) {
                    hideKeyboard(HideKeyboardStrategy.PRESS_KEY, "Done")
                }
                DriverUtils.wait(this, 1)
            }
            tryOptional {
                if (isKeyboardVisible()) {
                    hideKeyboard(HideKeyboardStrategy.TAP_OUTSIDE)
                }
            }
        }
    }
}

fun AppiumDriver<*>.isKeyboardVisible(): Boolean {
    return if (this is HasOnScreenKeyboard) isKeyboardShown else false
}

fun AppiumDriver<*>.selectAll(element: WebElement) {
    if (element is IOSElement) {
        element.longPressElementCenter(this)
        tryOptional {
            findElement(By.xpath("//XCUIElementTypeMenuItem[@name=\"Select All\"]")).click()
        }
    } else {
        element.sendKeys(Keys.chord(Keys.CONTROL, "a"))
    }
}

fun AppiumDriver<*>.deleteAll(element: WebElement) {
    selectAll(element)
    tryOptional {
        element.sendKeys(Keys.DELETE)
    }
}
