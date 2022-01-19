package com.testapp.tests.payments.flows.sliceit

import org.junit.Test

internal class SliceItUKFailureTest : BaseSliceItTest() {

    @Test
    fun `test payment slice it UK failure flow`() {
        testSliceItUK(false)
    }
}