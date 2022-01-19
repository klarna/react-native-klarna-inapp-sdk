package com.testapp.tests.payments.flows.paylater

import com.testapp.network.KlarnaApi
import com.testapp.utils.BillingAddressTestHelper
import com.testapp.utils.SessionHelper
import org.junit.Test

internal class PayLaterSEFailureTest: BasePayLaterTest() {

    @Test
    fun `test payment pay later sweden failure flow`() {
        val session = KlarnaApi.getSessionInfo(SessionHelper.getRequestSE())?.session
        testPayLater(false, session, BillingAddressTestHelper.getBillingInfoSE())
    }
}