package com.testapp.tests.payments.flows.threeds

import org.junit.Ignore
import org.junit.Test

internal class ThreeDSSuccessTest : BaseThreeDSTest() {

    @Test
    @Ignore
    fun `test 3ds successful flow`() {
        test3ds(true)
    }
}
