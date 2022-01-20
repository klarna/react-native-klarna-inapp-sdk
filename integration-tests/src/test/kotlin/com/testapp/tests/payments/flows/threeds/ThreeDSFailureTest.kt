package com.testapp.tests.payments.flows.threeds

import org.junit.Ignore
import org.junit.Test

internal class ThreeDSFailureTest : BaseThreeDSTest() {

    @Test
    @Ignore
    fun `test 3ds failure flow`() {
        test3ds(false)
    }
}