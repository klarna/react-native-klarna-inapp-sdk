package com.testapp.extensions

import io.appium.java_client.ios.IOSDriver
import org.openqa.selenium.remote.RemoteWebElement
import java.util.*

/**
 * http://appium.io/docs/en/writing-running-appium/ios/ios-xctest-mobile-gestures/
 *
 * @param direction Either 'up', 'down', 'left' or 'right'. The argument is mandatory
 * @param velocity This argument is optional and is only supported since Appium server version 1.19 and Xcode SDK version 11.4+. The value is measured in pixels per second and same values could behave differently on different devices depending on their display density. Higher values make swipe gesture faster (which usually scrolls larger areas if we apply it to a list) and lower values slow it down. Only values greater than zero have effect.
 * @param element The internal element identifier (as hexadecimal hash string) to swipe on. Application element will be used instead if this argument is not provided
 */
fun IOSDriver<*>.swipe(direction: String = "down", velocity: Int? = null, element: RemoteWebElement? = null) {
    val params: MutableMap<String, Any> = HashMap()
    params["direction"] = direction
    velocity?.let {
        params["velocity"] = it
    }
    element?.let {
        params["element"] = it.id
    }
    executeScript("mobile: swipe", params)
}

/**
 * http://appium.io/docs/en/writing-running-appium/ios/ios-xctest-mobile-gestures/
 *
 * @param element The internal element identifier (as hexadecimal hash string) to scroll on. Application element will be used if this argument is not set
 * @param name the accessibility id of the child element, to which scrolling is performed. The same result can be achieved by setting predicateString argument to 'name == accessibilityId'. Has no effect if element is not a container
 * @param direction Either 'up', 'down', 'left' or 'right'. The main difference from swipe call with the same argument is that scroll will try to move the current viewport exactly to the next/previous page (the term "page" means the content, which fits into a single device screen)
 * @param predicateString the NSPredicate locator of the child element, to which the scrolling should be performed. Has no effect if element is not a container
 * @param toVisible Boolean parameter. If set to true then asks to scroll to the first visible element in the parent container. Has no effect if element is not set
 */
fun IOSDriver<*>.scroll(
    element: RemoteWebElement? = null,
    name: String? = null,
    direction: String = "down",
    predicateString: String? = null,
    toVisible: Boolean? = null
) {
    val params: MutableMap<String, Any> = HashMap()
    element?.let {
        params["element"] = it.id
    }
    name?.let {
        params["name"] = it
    }
    params["direction"] = direction
    predicateString?.let {
        params["predicateString"] = it
    }
    toVisible?.let {
        params["toVisible"] = it
    }
    executeScript("mobile: scroll", params)
}