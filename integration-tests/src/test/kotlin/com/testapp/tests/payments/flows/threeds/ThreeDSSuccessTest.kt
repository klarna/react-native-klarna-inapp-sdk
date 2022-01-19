package com.testapp.tests.payments.flows.threeds

import org.junit.Test

internal class ThreeDSSuccessTest : BaseThreeDSTest() {

    @Test
    fun `test 3ds successful flow`() {
        test3ds(true)
    }
}
