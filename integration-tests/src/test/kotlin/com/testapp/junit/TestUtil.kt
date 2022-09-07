package com.testapp.junit

import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Assumptions

object TestUtil {

    fun assumeNoException(t: Throwable?) {
        Assumptions.assumingThat(t == null) {
            Assertions.fail("Exception occurred: ${t?.message}", t)
        }
    }

    fun fail(message: String?, cause: Throwable? = null) {
        cause?.let {
            Assertions.fail<Any>(message, cause)
        } ?: run {
            Assertions.fail<Any>(message)
        }
    }
}
