package com.testapp.tests.payments.flows.paylater

import com.testapp.constants.AppiumTestConstants
import com.testapp.network.KlarnaApi
import com.testapp.utils.BillingAddressTestHelper
import com.testapp.utils.SessionHelper
import io.github.artsok.RepeatedIfExceptionsTest
import org.junit.jupiter.api.Disabled

internal class PayLaterSESuccessTest : BasePayLaterTest() {

    @RepeatedIfExceptionsTest(repeats = AppiumTestConstants.DEFAULT_RETRY_COUNT)
    @Disabled
    fun `test payment pay later sweden successful flow`() {
        val session = KlarnaApi.getSessionInfo(SessionHelper.getRequestSE())?.session
        testPayLater(true, session, BillingAddressTestHelper.getBillingInfoSE())
    }
}