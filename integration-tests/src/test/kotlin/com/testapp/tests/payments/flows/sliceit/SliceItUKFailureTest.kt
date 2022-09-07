package com.testapp.tests.payments.flows.sliceit

import com.testapp.constants.AppiumTestConstants
import io.github.artsok.RepeatedIfExceptionsTest
import org.junit.jupiter.api.Disabled

internal class SliceItUKFailureTest : BaseSliceItTest() {

    @RepeatedIfExceptionsTest(repeats = AppiumTestConstants.DEFAULT_RETRY_COUNT)
    @Disabled
    fun `test payment slice it UK failure flow`() {
        testSliceItUK(false)
    }
}