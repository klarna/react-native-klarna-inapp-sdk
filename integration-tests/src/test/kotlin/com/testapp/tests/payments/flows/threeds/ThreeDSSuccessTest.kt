package com.testapp.tests.payments.flows.threeds

import com.testapp.constants.AppiumTestConstants
import io.github.artsok.RepeatedIfExceptionsTest
import org.junit.jupiter.api.Disabled

internal class ThreeDSSuccessTest : BaseThreeDSTest() {

    @RepeatedIfExceptionsTest(repeats = AppiumTestConstants.DEFAULT_RETRY_COUNT)
    @Disabled
    fun `test 3ds successful flow`() {
        test3ds(true)
    }
}
