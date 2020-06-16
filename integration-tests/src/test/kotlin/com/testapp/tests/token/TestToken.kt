package com.testapp.tests.token

import com.testapp.base.BaseAppiumTest
import com.testapp.base.RetryRule
import com.testapp.network.KlarnaApi
import com.testapp.utils.DriverUtils
import com.testapp.utils.SessionHelper
import io.appium.java_client.MobileBy
import org.junit.Assert
import org.junit.BeforeClass
import org.junit.Rule
import org.junit.Test
import org.openqa.selenium.support.ui.ExpectedConditions

internal class TestToken : BaseAppiumTest() {

    companion object {
        @JvmStatic
        @BeforeClass
        fun setup() {
            testName = TestToken::class.java.simpleName
            BaseAppiumTest.setup()
        }
    }

    @Rule
    @JvmField
    var retryRule = RetryRule(retryCount, ignoreOnFailure)

    @Test
    fun `test get and set token to the test app`() {
        val token = KlarnaApi.getClientToken(SessionHelper.getRequestSE())
        Assert.assertNotNull(token)
        DriverUtils.getWaiter(driver).until(ExpectedConditions.presenceOfElementLocated(MobileBy.ByAccessibilityId("setTokenInput"))).apply {
            sendKeys(token)
            Assert.assertEquals(token, text)
        }
    }
}