package com.testapp.tests.payments.flows.paynow

import org.junit.Ignore
import org.junit.Test

internal class PayNowSofortFailureTest : BasePayNowTest() {

    @Test
    @Ignore
    fun `test payment pay now DE Sofort failure flow`() {
        testPayNowSofort(false)
    }
}