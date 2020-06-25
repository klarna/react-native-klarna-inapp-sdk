package com.testapp.tests.appium

import com.testapp.base.BaseAppiumTest
import com.testapp.base.RetryRule
import com.testapp.utils.ByRnId
import com.testapp.utils.DriverUtils
import org.junit.BeforeClass
import org.junit.Rule
import org.junit.Test
import org.openqa.selenium.support.ui.ExpectedConditions

internal class TestAppiumServer : BaseAppiumTest() {

    companion object {
        @JvmStatic
        @BeforeClass
        fun setup() {
            testName = TestAppiumServer::class.java.simpleName
            BaseAppiumTest.setup()
        }
    }

    @Rule
    @JvmField
    var retryRule = RetryRule(retryCount, ignoreOnFailure)

    @Test
    fun testAppiumServer() {
        DriverUtils.getWaiter(driver).until(ExpectedConditions.presenceOfElementLocated(ByRnId(driver,"initButton_pay_now"))).click()
    }
}