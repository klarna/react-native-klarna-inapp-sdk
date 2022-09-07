package com.testapp.tests.payments.flows.paylater

import com.testapp.constants.AppiumTestConstants
import com.testapp.network.KlarnaApi
import com.testapp.utils.BillingAddressTestHelper
import com.testapp.utils.SessionHelper
import io.github.artsok.RepeatedIfExceptionsTest
import org.junit.jupiter.api.Disabled

internal class PayLaterSEFailureTest : BasePayLaterTest() {

    @RepeatedIfExceptionsTest(repeats = AppiumTestConstants.DEFAULT_RETRY_COUNT)
    @Disabled
    fun `test payment pay later sweden failure flow`() {
        val session = KlarnaApi.getSessionInfo(SessionHelper.getRequestSE())?.session
        testPayLater(false, session, BillingAddressTestHelper.getBillingInfoSE())
    }
}