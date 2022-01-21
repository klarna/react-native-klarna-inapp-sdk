package com.testapp.tests.payments.flows.paylater

import com.testapp.network.KlarnaApi
import com.testapp.utils.BillingAddressTestHelper
import com.testapp.utils.SessionHelper
import org.junit.Test

internal class PayLaterNOFailureTest: BasePayLaterTest() {

    @Test
    fun `test payment pay later norway failure flow`() {
        val session = KlarnaApi.getSessionInfo(SessionHelper.getRequestNO())?.session
        testPayLater(false, session, BillingAddressTestHelper.getBillingInfoNO())
    }
}