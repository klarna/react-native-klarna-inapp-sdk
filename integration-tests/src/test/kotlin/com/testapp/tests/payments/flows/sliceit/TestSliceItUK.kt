package com.testapp.tests.payments.flows.paynow

import com.testapp.base.BaseAppiumTest
import com.testapp.base.PaymentCategory
import com.testapp.base.RetryRule
import com.testapp.network.KlarnaApi
import com.testapp.utils.BillingAddressTestHelper
import com.testapp.utils.ByRnId
import com.testapp.utils.DriverUtils
import com.testapp.utils.PaymentFlowsTestHelper
import com.testapp.utils.SessionHelper
import com.testapp.utils.WebViewTestHelper
import io.appium.java_client.android.AndroidDriver
import org.junit.Assert
import org.junit.BeforeClass
import org.junit.Rule
import org.junit.Test
import org.openqa.selenium.By
import org.openqa.selenium.support.ui.ExpectedConditions

internal class TestSliceItUK : BaseAppiumTest(){
    companion object {

        @JvmStatic
        @BeforeClass
        fun setup() {
            testName = TestSliceItUK::class.java.simpleName
            BaseAppiumTest.setup()
        }
    }

    @Rule
    @JvmField
    var retryRule = RetryRule(retryCount, ignoreOnFailure)

    @Test
    fun `test payment slice it UK successful flow`() {
        testSliceItUK(true)
    }

    @Test
    fun `test payment slice it UK failure flow`() {
        testSliceItUK(false)
    }

    private fun testSliceItUK(success: Boolean){
        val session = KlarnaApi.getSessionInfo(SessionHelper.getRequestUK())?.session
        if(session?.client_token == null || !session.payment_method_categories.map { it.identifier }.contains(PaymentCategory.SLICE_IT.value)){
            return
        }
        val token = session.client_token
        initLoadSDK(token, PaymentCategory.SLICE_IT.value)
        DriverUtils.switchContextToWebView(driver)

        val mainWindow = WebViewTestHelper.findWindowFor(driver, By.id("klarna-some-hardcoded-instance-id-main"))
        mainWindow?.let {
            driver.switchTo().window(it)
        } ?: Assert.fail("Main window wasn't found")
        DriverUtils.getWaiter(driver).until(ExpectedConditions.frameToBeAvailableAndSwitchToIt("klarna-some-hardcoded-instance-id-main"))

        DriverUtils.getWaiter(driver).until(ExpectedConditions.frameToBeAvailableAndSwitchToIt(By.xpath("//*[@id=\"pay-later-slice-it-slice-it-by-card-card\"]/iframe")))

        PaymentFlowsTestHelper.fillCardInfo(driver)

        DriverUtils.switchContextToNative(driver)

        driver.hideKeyboard()

        PaymentFlowsTestHelper.dismissConsole()

        try {
            driver.findElement(ByRnId(driver, "authorizeButton_${PaymentCategory.SLICE_IT.value}")).click()
        } catch (t: Throwable){
            driver.findElementByAndroidUIAutomator("new UiScrollable(new UiSelector().scrollable(true).instance(0)).scrollIntoView(new UiSelector().description(\"authorizeButton_${PaymentCategory.SLICE_IT.value}\"))")
            DriverUtils.getWaiter(driver).until(ExpectedConditions.presenceOfElementLocated(ByRnId(driver, "authorizeButton_${PaymentCategory.SLICE_IT.value}"))).click()
        }

        val billing = BillingAddressTestHelper.getBillingInfoUK()
        if(!success) {
            BillingAddressTestHelper.setEmailFlag(billing, BillingAddressTestHelper.EMAIL_FLAG_REJECTED)
        }
        PaymentFlowsTestHelper.fillBillingAddress(driver, billing)

        if(!success) {
            val refusedTextBy = By.xpath("//*[@id=\"message-component-root\"]")
            val refusedText =
                    DriverUtils.getWaiter(driver).until(ExpectedConditions.presenceOfElementLocated(refusedTextBy))
            with(refusedText.text.toLowerCase()) {
                assert(this.contains("sorry") || this.contains("unfortunately"))
            }
        } else {
            DriverUtils.switchContextToNative(driver)
            var response = PaymentFlowsTestHelper.readConsoleMessage(driver, "authToken")?.text

            PaymentFlowsTestHelper.checkAuthorizeResponse(response, true)
        }
    }
}