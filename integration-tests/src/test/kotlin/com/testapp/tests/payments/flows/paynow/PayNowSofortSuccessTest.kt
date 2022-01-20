package com.testapp.tests.payments.flows.paynow

import org.junit.Ignore
import org.junit.Test

internal class PayNowSofortSuccessTest : BasePayNowTest() {

    @Test
    @Ignore
    fun `test payment pay now DE Sofort successful flow`() {
        testPayNowSofort(true)
    }
}
