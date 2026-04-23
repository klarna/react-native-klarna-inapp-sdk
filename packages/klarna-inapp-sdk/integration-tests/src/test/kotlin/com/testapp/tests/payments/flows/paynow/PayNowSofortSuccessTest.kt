package com.testapp.tests.payments.flows.paynow

import com.testapp.constants.AppiumTestConstants
import io.github.artsok.RepeatedIfExceptionsTest
import org.junit.jupiter.api.Disabled

internal class PayNowSofortSuccessTest : BasePayNowTest() {

    @RepeatedIfExceptionsTest(repeats = AppiumTestConstants.DEFAULT_RETRY_COUNT)
    @Disabled
    fun `test payment pay now DE Sofort successful flow`() {
        testPayNowSofort(true)
    }
}
