package com.testapp.tests.appium

import com.testapp.base.BaseAppiumTest
import com.testapp.utils.ByRnId
import com.testapp.utils.DriverUtils
import org.junit.Test
import org.openqa.selenium.support.ui.ExpectedConditions

internal class TestAppiumServer : BaseAppiumTest() {

    @Test
    fun testAppiumServer() {
        DriverUtils.getWaiter(driver)
            .until(ExpectedConditions.presenceOfElementLocated(ByRnId(driver, "initButton_pay_now"))).click()
    }
}