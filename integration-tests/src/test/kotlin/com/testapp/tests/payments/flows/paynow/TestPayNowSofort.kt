package com.testapp.tests.payments.flows.paynow

import com.testapp.base.BaseAppiumTest
import com.testapp.base.PaymentCategory
import com.testapp.base.RetryRule
import com.testapp.network.KlarnaApi
import com.testapp.utils.*
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

internal class TestPayNowSofort : BaseAppiumTest(){
    companion object {

        @JvmStatic
        @BeforeClass
        fun setup() {
            testName = TestPayNowSofort::class.java.simpleName
            BaseAppiumTest.setup()
        }
    }

    @Rule
    @JvmField
    var retryRule = RetryRule(retryCount, ignoreOnFailure)

    @Test
    fun `test payment pay now DE Sofort successful flow`() {
        testPayNowSofort(true)
    }

    @Test
    fun `test payment pay now DE Sofort failure flow`() {
        testPayNowSofort(false)
    }

    private fun testPayNowSofort(success: Boolean){
        val session = KlarnaApi.getSessionInfo(SessionHelper.getRequestDE())?.session
        if(session?.client_token == null || !session.payment_method_categories.map { it.identifier }.contains(PaymentCategory.PAY_NOW.value)){
            return
        }
        val token = session.client_token

        DriverUtils.getWaiter(driver).until(ExpectedConditions.presenceOfElementLocated(ByRnId(driver, "setTokenInput"))).apply {
            sendKeys(token)
            Assert.assertEquals(token, text)
        }
        driver.findElement(ByRnId(driver,"initButton_${PaymentCategory.PAY_NOW.value}")).click()
        //wait for init response
        PaymentFlowsTestHelper.readConsoleMessage(driver, "{}")

        driver.findElement(ByRnId(driver,"loadButton_${PaymentCategory.PAY_NOW.value}")).click()
        DriverUtils.switchContextToWebView(driver)

        val mainWindow = WebViewTestHelper.findWindowFor(driver, By.id("klarna-some-hardcoded-instance-id-main"))
        mainWindow?.let {
            driver.switchTo().window(it)
        } ?: Assert.fail("Main window wasn't found")
        DriverUtils.getWaiter(driver).until(ExpectedConditions.frameToBeAvailableAndSwitchToIt("klarna-some-hardcoded-instance-id-main"))

        DriverUtils.getWaiter(driver).until(ExpectedConditions.presenceOfElementLocated(By.id("installments-card|-1"))).click()

        DriverUtils.getWaiter(driver).until(ExpectedConditions.frameToBeAvailableAndSwitchToIt(By.xpath("//*[@id=\"pay-now-card\"]/iframe")))

        PaymentFlowsTestHelper.fillCardInfo(driver)

        DriverUtils.switchContextToNative(driver)

        driver.hideKeyboard()

        PaymentFlowsTestHelper.dismissConsole()

        try {
            driver.findElement(ByRnId(driver, "authorizeButton_${PaymentCategory.PAY_NOW.value}")).click()
        } catch (t: Throwable){
            if(DriverUtils.isAndroid(driver)) {
                (driver as AndroidDriver).findElementByAndroidUIAutomator("new UiScrollable(new UiSelector().scrollable(true).instance(0)).scrollIntoView(new UiSelector().text(\"${PaymentCategory.PAY_LATER.value}\"))")
            } else if(DriverUtils.isIos(driver)){
                //TODO scroll down in ios
            }
            DriverUtils.getWaiter(driver).until(ExpectedConditions.presenceOfElementLocated(ByRnId(driver, "authorizeButton_${PaymentCategory.PAY_NOW.value}"))).click()
        }

        val billing = BillingAddressTestHelper.getBillingInfoDE()
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