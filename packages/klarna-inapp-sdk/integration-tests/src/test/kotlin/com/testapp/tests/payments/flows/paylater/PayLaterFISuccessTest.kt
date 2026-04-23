package com.testapp.tests.payments.flows.paylater

import com.testapp.constants.AppiumTestConstants
import com.testapp.network.KlarnaApi
import com.testapp.utils.BillingAddressTestHelper
import com.testapp.utils.SessionHelper
import io.github.artsok.RepeatedIfExceptionsTest

internal class PayLaterFISuccessTest : BasePayLaterTest() {

    @RepeatedIfExceptionsTest(repeats = AppiumTestConstants.DEFAULT_RETRY_COUNT)
    fun `test payment pay later finland successful flow`() {
        val session = KlarnaApi.getSessionInfo(SessionHelper.getRequestFI())?.session
        testPayLater(true, session, BillingAddressTestHelper.getBillingInfoFI())
    }
}