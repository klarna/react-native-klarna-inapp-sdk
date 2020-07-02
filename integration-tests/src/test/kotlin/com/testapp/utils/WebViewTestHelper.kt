package com.testapp.utils

import io.appium.java_client.MobileElement
import io.appium.java_client.android.AndroidDriver
import org.openqa.selenium.By
import org.openqa.selenium.support.ui.ExpectedConditions
import org.openqa.selenium.support.ui.WebDriverWait
import java.util.ArrayList

internal object WebViewTestHelper {

    /**
     * '
     * Tries to find the window that contains the given iFrame and that iFrame contains the given element
     *
     * @param driver         The driver we are using to connect to Appium
     * @param iframeLocator  The locator for the iFrame we wish to find
     * @param elementLocator The locator for the element we wish to find
     * @return The found Window handle, null otherwise
     */
    fun findWindowFor(driver: AndroidDriver<MobileElement>, iframeLocator: By?, elementLocator: By? = null): String? {
        DriverUtils.switchContextToWebView(driver)
        val waitTimeInSec = 1
        val initialWindow = driver.windowHandle
        val windows = ArrayList(driver.windowHandles)
        val wait = WebDriverWait(driver, waitTimeInSec.toLong())
        for (window in windows) {
            try {
                driver.switchTo().window(window)
                if (iframeLocator != null) {
                    wait.until(
                            ExpectedConditions.frameToBeAvailableAndSwitchToIt(
                                    iframeLocator
                            )
                    )
                }
                if (elementLocator != null) {
                    wait.until(ExpectedConditions.presenceOfElementLocated(elementLocator))
                }
                driver.switchTo().window(initialWindow)
                return window
            } catch (e: Exception) { // do nothing
            }
        }
        return null
    }

}