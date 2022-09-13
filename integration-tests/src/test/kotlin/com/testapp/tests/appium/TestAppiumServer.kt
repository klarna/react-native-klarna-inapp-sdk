package com.testapp.tests.appium

import com.testapp.base.BaseAppiumMethodTest
import com.testapp.constants.AppiumTestConstants
import com.testapp.utils.ByRnId
import com.testapp.utils.DriverUtils
import io.github.artsok.RepeatedIfExceptionsTest
import org.openqa.selenium.support.ui.ExpectedConditions

internal class TestAppiumServer : BaseAppiumMethodTest() {

    @RepeatedIfExceptionsTest(repeats = AppiumTestConstants.DEFAULT_RETRY_COUNT)
    fun testAppiumServer() {
        DriverUtils.getWaiter(driver)
            .until(ExpectedConditions.presenceOfElementLocated(ByRnId(driver, "navKlarnaPayments"))).click()
    }
}