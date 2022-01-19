package com.testapp.tests.payments.flows.paylater

import com.testapp.network.KlarnaApi
import com.testapp.utils.BillingAddressTestHelper
import com.testapp.utils.SessionHelper
import org.junit.Test

internal class PayLaterSESuccessTest : BasePayLaterTest() {

    @Test
    fun `test payment pay later sweden successful flow`() {
        val session = KlarnaApi.getSessionInfo(SessionHelper.getRequestSE())?.session
        testPayLater(true, session, BillingAddressTestHelper.getBillingInfoSE())
    }
}