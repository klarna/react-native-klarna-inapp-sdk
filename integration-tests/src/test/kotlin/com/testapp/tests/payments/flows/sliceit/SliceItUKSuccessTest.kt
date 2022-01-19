package com.testapp.tests.payments.flows.sliceit

import org.junit.Test

internal class SliceItUKSuccessTest : BaseSliceItTest() {

    @Test
    fun `test payment slice it UK successful flow`() {
        testSliceItUK(true)
    }
}
