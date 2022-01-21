package com.testapp.tests.payments.flows.paylater

import com.testapp.network.KlarnaApi
import com.testapp.utils.BillingAddressTestHelper
import com.testapp.utils.SessionHelper
import org.junit.Test

internal class PayLaterFIFailureTest: BasePayLaterTest() {

    @Test
    fun `test payment pay later finland failure flow`() {
        val session = KlarnaApi.getSessionInfo(SessionHelper.getRequestFI())?.session
        testPayLater(false, session, BillingAddressTestHelper.getBillingInfoFI())
    }
}