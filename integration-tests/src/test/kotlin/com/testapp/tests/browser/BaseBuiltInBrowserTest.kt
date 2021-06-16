package com.testapp.tests.browser

import com.testapp.base.BaseAppiumTest
import com.testapp.base.PaymentCategory
import com.testapp.model.Session
import com.testapp.utils.DriverUtils
import com.testapp.utils.WebViewTestHelper
import io.appium.java_client.MobileBy
import org.junit.Assert
import org.openqa.selenium.By
import org.openqa.selenium.support.ui.ExpectedConditions

internal abstract class BaseBuiltInBrowserTest : BaseAppiumTest() {

    protected fun testLinks(session: Session?, category: PaymentCategory, linkLocator: By) {

        if (session?.client_token == null || !session.payment_method_categories.map { it.identifier }
                .contains(category.value)) {
            return
        }
        val token = session.client_token
        initLoadSDK(token, category)
        if (android()) {
            DriverUtils.switchContextToWebView(driver)
            val mainWindow = WebViewTestHelper.findWindowFor(driver, By.id("klarna-some-hardcoded-instance-id-main"))
            mainWindow?.let {
                driver.switchTo().window(it)
            } ?: Assert.fail("Main window wasn't found")
            DriverUtils.switchToIframe(driver, "klarna-some-hardcoded-instance-id-main")
        } else {
            DriverUtils.getWaiter(driver)
                .until(ExpectedConditions.presenceOfElementLocated(By.xpath("//XCUIElementTypeOther[@name='Payment View']")))
            DriverUtils.getWaiter(driver)
                .until(ExpectedConditions.presenceOfElementLocated(By.xpath("//XCUIElementTypeStaticText[@name='TESTDRIVE']")))
        }

        var tries = 0
        var openedLink = false
        do {
            try {
                DriverUtils.wait(driver, 1)
                val links = DriverUtils.getWaiter(driver)
                    .until(ExpectedConditions.presenceOfAllElementsLocatedBy(linkLocator))
                    .filter { !it.text.isNullOrEmpty() }
                links.first().click()
                if (android()) {
                    DriverUtils.waitForActivity(
                        driver,
                        "com.klarna.mobile.sdk.core.natives.browser.ui.InternalBrowserActivity",
                        2
                    )
                    DriverUtils.switchContextToNative(driver)
                    driver.findElement(MobileBy.id("closeIcon")).click()
                    DriverUtils.switchContextToWebView(driver)
                } else {
                    DriverUtils.getWaiter(driver)
                        .until(ExpectedConditions.presenceOfAllElementsLocatedBy(By.xpath("//XCUIElementTypeNavigationBar[@name='KlarnaMobileSDK.InAppBrowserView']")))
                    driver.findElement(By.xpath("//XCUIElementTypeButton[@name='Stop']")).click()
                }

                openedLink = true
            } catch (t: Throwable) {
                tries++
            }
        } while (!openedLink && tries < 3)
    }
}
