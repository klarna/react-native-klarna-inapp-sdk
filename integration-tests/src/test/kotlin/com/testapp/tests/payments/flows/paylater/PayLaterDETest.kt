package com.testapp.tests.payments.flows.paylater

import com.testapp.network.KlarnaApi
import com.testapp.utils.BillingAddressTestHelper
import com.testapp.utils.SessionHelper
import org.junit.Test

internal class PayLaterDETest : BasePayLaterTest() {

    @Test
    fun `test payment pay later germany successful flow`() {
        val session = KlarnaApi.getSessionInfo(SessionHelper.getRequestDE())?.session
        testPayLater(true, session, BillingAddressTestHelper.getBillingInfoDE())
    }

    @Test
    fun `test payment pay later germany failure flow`() {
        val session = KlarnaApi.getSessionInfo(SessionHelper.getRequestDE())?.session
        testPayLater(false, session, BillingAddressTestHelper.getBillingInfoDE())
    }
}