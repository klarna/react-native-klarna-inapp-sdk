package com.testapp.tests.payments.flows.paylater

import com.testapp.constants.AppiumTestConstants
import com.testapp.network.KlarnaApi
import com.testapp.utils.BillingAddressTestHelper
import com.testapp.utils.SessionHelper
import io.github.artsok.RepeatedIfExceptionsTest

internal class PayLaterFIFailureTest : BasePayLaterTest() {

    @RepeatedIfExceptionsTest(repeats = AppiumTestConstants.DEFAULT_RETRY_COUNT)
    fun `test payment pay later finland failure flow`() {
        val session = KlarnaApi.getSessionInfo(SessionHelper.getRequestFI())?.session
        testPayLater(false, session, BillingAddressTestHelper.getBillingInfoFI())
    }
}