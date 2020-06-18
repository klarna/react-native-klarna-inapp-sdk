package com.testapp.tests.payments.flows.paylater

import com.testapp.base.BaseAppiumTest
import com.testapp.base.PaymentCategory
import com.testapp.base.RetryRule
import com.testapp.model.Session
import com.testapp.network.KlarnaApi
import com.testapp.utils.*
import io.appium.java_client.android.AndroidDriver
import org.junit.Assert
import org.junit.BeforeClass
import org.junit.Rule
import org.junit.Test
import org.openqa.selenium.By
import org.openqa.selenium.support.ui.ExpectedConditions

internal class TestPayLater : BaseAppiumTest() {

    companion object {
        @JvmStatic
        @BeforeClass
        fun setup() {
            testName = TestPayLater::class.java.simpleName
            BaseAppiumTest.setup()
        }
    }

    @Rule
    @JvmField
    var retryRule = RetryRule(retryCount, ignoreOnFailure)

    @Test
    fun `test payment pay later sweden successful flow`() {
        val session = KlarnaApi.getSessionInfo(SessionHelper.getRequestSE())?.session
        testSuccess(session, BillingAddressTestHelper.getBillingInfoSE())
    }

    @Test
    fun `test payment pay later sweden failure flow`() {
        val session = KlarnaApi.getSessionInfo(SessionHelper.getRequestSE())?.session
        testFailure(session, BillingAddressTestHelper.getBillingInfoSE())
    }

    @Test
    fun `test payment pay later norway successful flow`() {
        val session = KlarnaApi.getSessionInfo(SessionHelper.getRequestNO())?.session
        testSuccess(session, BillingAddressTestHelper.getBillingInfoNO())
    }

    @Test
    fun `test payment pay later norway failure flow`() {
        val session = KlarnaApi.getSessionInfo(SessionHelper.getRequestNO())?.session
        testFailure(session, BillingAddressTestHelper.getBillingInfoNO())
    }

    @Test
    fun `test payment pay later finland successful flow`() {
        val session = KlarnaApi.getSessionInfo(SessionHelper.getRequestFI())?.session
        testSuccess(session, BillingAddressTestHelper.getBillingInfoFI())
    }

    @Test
    fun `test payment pay later finland failure flow`() {
        val session = KlarnaApi.getSessionInfo(SessionHelper.getRequestFI())?.session
        testFailure(session, BillingAddressTestHelper.getBillingInfoFI())
    }

    @Test
    fun `test payment pay later germany successful flow`() {
        val session = KlarnaApi.getSessionInfo(SessionHelper.getRequestDE())?.session
        testSuccess(session, BillingAddressTestHelper.getBillingInfoDE())
    }

    @Test
    fun `test payment pay later germany failure flow`() {
        val session = KlarnaApi.getSessionInfo(SessionHelper.getRequestDE())?.session
        testFailure(session, BillingAddressTestHelper.getBillingInfoDE())
    }

    private fun testSuccess(session: Session?, billing: LinkedHashMap<String, String?>) {
        if(session?.client_token == null || !session.payment_method_categories.map { it.identifier }.contains(PaymentCategory.PAY_LATER.value)){
            return
        }
        val token = session.client_token

        DriverUtils.getWaiter(driver).until(ExpectedConditions.presenceOfElementLocated(ByRnId(driver, "setTokenInput"))).apply {
            sendKeys(token)
            Assert.assertEquals(token, text)
        }
        driver.findElement(ByRnId(driver,"initButton_${PaymentCategory.PAY_LATER}")).click()
        //wait for init response
        PaymentFlowsTestHelper.readConsoleMessage(driver, "{}")

        driver.findElement(ByRnId(driver,"loadButton_${PaymentCategory.PAY_LATER}")).click()
        DriverUtils.switchContextToWebView(driver)
        val mainWindow = WebViewTestHelper.findWindowFor(driver, By.id("klarna-some-hardcoded-instance-id-fullscreen"))
        mainWindow?.let {
            driver.switchTo().window(it)
        } ?: Assert.fail("Main window wasn't found")
        DriverUtils.getWaiter(driver).until(ExpectedConditions.frameToBeAvailableAndSwitchToIt("klarna-some-hardcoded-instance-id-fullscreen"))
        DriverUtils.switchContextToNative(driver)

        try {
            driver.findElement(ByRnId(driver, "authorizeButton_${PaymentCategory.PAY_LATER}")).click()
        } catch (t: Throwable){
            if(DriverUtils.isAndroid(driver)) {
                (driver as AndroidDriver).findElementByAndroidUIAutomator("new UiScrollable(new UiSelector().scrollable(true).instance(0)).scrollIntoView(new UiSelector().text(\"slice_it\"))")
            } else if(DriverUtils.isIos(driver)){
                //TODO scroll down in ios
            }
            DriverUtils.getWaiter(driver).until(ExpectedConditions.presenceOfElementLocated(ByRnId(driver, "authorizeButton_${PaymentCategory.PAY_LATER}"))).click()
        }

        PaymentFlowsTestHelper.fillBillingAddress(driver, billing)

        val submitButtonBy = By.xpath("//*[@id=\"confirmation__footer-button-wrapper\"]/div/button")
        DriverUtils.getWaiter(driver).until(ExpectedConditions.presenceOfElementLocated(submitButtonBy)).click()

        DriverUtils.switchContextToNative(driver)
        var response = PaymentFlowsTestHelper.readConsoleMessage(driver, "authToken")?.text

        PaymentFlowsTestHelper.checkAuthorizeResponse(response, true)
    }

    private fun testFailure(session: Session?, billing: LinkedHashMap<String, String?>) {
        if(session?.client_token == null || !session.payment_method_categories.map { it.identifier }.contains(PaymentCategory.PAY_LATER.value)){
            return
        }
        val token = session.client_token

        DriverUtils.getWaiter(driver).until(ExpectedConditions.presenceOfElementLocated(ByRnId(driver, "setTokenInput"))).apply {
            sendKeys(token)
            Assert.assertEquals(token, text)
        }
        BillingAddressTestHelper.setEmailFlag(billing, BillingAddressTestHelper.EMAIL_FLAG_REJECTED)
        DriverUtils.getWaiter(driver).until(ExpectedConditions.presenceOfElementLocated(ByRnId(driver,"initButton_${PaymentCategory.PAY_LATER}"))).click()
        //wait for init response
        DriverUtils.getWaiter(driver).until(ExpectedConditions.presenceOfElementLocated(By.xpath("//android.widget.TextView[contains(@text, '{}')]")))

        DriverUtils.getWaiter(driver).until(ExpectedConditions.presenceOfElementLocated(ByRnId(driver,"loadButton_${PaymentCategory.PAY_LATER}"))).click()
        DriverUtils.switchContextToWebView(driver)
        val mainWindow = WebViewTestHelper.findWindowFor(driver, By.id("klarna-some-hardcoded-instance-id-fullscreen"))
        mainWindow?.let {
            driver.switchTo().window(it)
        } ?: Assert.fail("Main window wasn't found")
        DriverUtils.getWaiter(driver).until(ExpectedConditions.frameToBeAvailableAndSwitchToIt("klarna-some-hardcoded-instance-id-fullscreen"))
        DriverUtils.switchContextToNative(driver)

        try {
            driver.findElement(ByRnId(driver, "authorizeButton_${PaymentCategory.PAY_LATER}")).click()
        } catch (t: Throwable){
            if(DriverUtils.isAndroid(driver)) {
                (driver as AndroidDriver).findElementByAndroidUIAutomator("new UiScrollable(new UiSelector().scrollable(true).instance(0)).scrollIntoView(new UiSelector().text(\"slice_it\"))")
            } else if(DriverUtils.isIos(driver)){
                //TODO scroll down in ios
            }
            DriverUtils.getWaiter(driver).until(ExpectedConditions.presenceOfElementLocated(ByRnId(driver, "authorizeButton_${PaymentCategory.PAY_LATER}"))).click()
        }

        PaymentFlowsTestHelper.fillBillingAddress(driver, billing)

        val refusedTextBy = By.xpath("//*[@id=\"message-component-root\"]")
        val refusedText =
                DriverUtils.getWaiter(driver).until(ExpectedConditions.presenceOfElementLocated(refusedTextBy))
        with(refusedText.text.toLowerCase()) {
            assert(this.contains("sorry") || this.contains("unfortunately"))
        }
    }
}