package com.testapp.tests.payments.flows.paylater

import com.testapp.network.KlarnaApi
import com.testapp.utils.BillingAddressTestHelper
import com.testapp.utils.SessionHelper
import org.junit.Test

internal class PayLaterDEFailureTest: BasePayLaterTest() {

    @Test
    fun `test payment pay later germany failure flow`() {
        val session = KlarnaApi.getSessionInfo(SessionHelper.getRequestDE())?.session
        testPayLater(false, session, BillingAddressTestHelper.getBillingInfoDE())
    }
}