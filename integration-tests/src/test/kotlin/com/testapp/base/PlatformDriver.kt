package com.testapp.base

import io.appium.java_client.AppiumDriver
import org.openqa.selenium.Capabilities
import org.openqa.selenium.WebElement
import java.net.URL

internal class PlatformDriver<T : WebElement>(remoteAddress: URL, capabilities: Capabilities, val platfom: Platform) : AppiumDriver<T>(remoteAddress, capabilities)