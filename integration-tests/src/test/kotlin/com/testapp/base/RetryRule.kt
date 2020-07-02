package com.testapp.base

import com.testapp.constants.AppiumTestConstants
import org.junit.Assume
import org.junit.rules.TestRule
import org.junit.runner.Description
import org.junit.runners.model.Statement

class RetryRule(
        private val retryCount: Int,
        private val ignoreOnRetriesFailure: Boolean
) : TestRule {

    private val whiteListedExceptions = arrayListOf(
            "chrome not reachable",
            "session not created",
            "Could not proxy"
    )

    override fun apply(base: Statement, description: Description): Statement {
        return statement(base, description)
    }

    private fun statement(base: Statement, description: Description): Statement {
        return object : Statement() {
            @Throws(Throwable::class)
            override fun evaluate() {
                var caughtThrowable: Throwable? = null
                for (i in 0 until retryCount) {
                    try {
                        base.evaluate()
                        return
                    } catch (t: Throwable) {
                        caughtThrowable = t
                        System.err.println(description.getDisplayName().toString() + ": run " + (i + 1) + " failed.")
                    }
                }
                System.err.println(description.getDisplayName().toString() + ": giving up after " + retryCount + " failures.")
                if (ignoreOnRetriesFailure || isWhiteListedException(caughtThrowable)) {
                    Assume.assumeNoException(caughtThrowable)
                } else {
                    throw caughtThrowable!!
                }
            }
        }
    }

    private fun <T : Throwable>isWhiteListedException(caughtThrowable: T?): Boolean {
        caughtThrowable?.let { throwable ->
            return whiteListedExceptions.any { throwable.message?.contains(it) ?: false }
        } ?: return true
    }
}