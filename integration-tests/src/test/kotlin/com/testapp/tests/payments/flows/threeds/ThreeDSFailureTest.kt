package com.testapp.tests.payments.flows.threeds

import org.junit.Test

internal class ThreeDSFailureTest : BaseThreeDSTest() {

    @Test
    fun `test 3ds failure flow`() {
        test3ds(false)
    }
}