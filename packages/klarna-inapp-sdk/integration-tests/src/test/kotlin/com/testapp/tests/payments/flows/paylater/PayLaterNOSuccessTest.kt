package com.testapp.tests.payments.flows.paylater

import com.testapp.constants.AppiumTestConstants
import com.testapp.network.KlarnaApi
import com.testapp.utils.BillingAddressTestHelper
import com.testapp.utils.SessionHelper
import io.github.artsok.RepeatedIfExceptionsTest

internal class PayLaterNOSuccessTest : BasePayLaterTest() {

    @RepeatedIfExceptionsTest(repeats = AppiumTestConstants.DEFAULT_RETRY_COUNT)
    fun `test payment pay later norway successful flow`() {
        val session = KlarnaApi.getSessionInfo(SessionHelper.getRequestNO())?.session
        testPayLater(true, session, BillingAddressTestHelper.getBillingInfoNO())
    }
}