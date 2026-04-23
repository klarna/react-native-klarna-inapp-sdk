package com.testapp.junit.extension.testwatcher.browserstack

import com.testapp.extensions.setTestStatus
import com.testapp.junit.TestResult
import com.testapp.junit.getDriver
import org.junit.jupiter.api.extension.ExtensionContext
import org.junit.jupiter.api.extension.TestWatcher
import java.util.*

class BrowserStackEachTestWatcher : TestWatcher {

    private fun updateStatus(testResult: TestResult) {
        val driver = testResult.context?.getDriver()
        when (testResult) {
            is TestResult.Successful -> driver?.setTestStatus(true)
            is TestResult.Failed -> driver?.setTestStatus(false, testResult.cause?.message)
            is TestResult.Aborted -> driver?.setTestStatus(false, testResult.cause?.message)
            is TestResult.Disabled -> driver?.setTestStatus(true, testResult.reason?.get())
            // null -> driver?.setTestStatus(false, "unknown")
        }
    }

    override fun testSuccessful(context: ExtensionContext?) {
        super.testSuccessful(context)
        updateStatus(TestResult.Successful(context))
    }

    override fun testFailed(context: ExtensionContext?, cause: Throwable?) {
        super.testFailed(context, cause)
        updateStatus(TestResult.Failed(context, cause))
    }

    override fun testAborted(context: ExtensionContext?, cause: Throwable?) {
        super.testAborted(context, cause)
        updateStatus(TestResult.Aborted(context, cause))
    }

    override fun testDisabled(context: ExtensionContext?, reason: Optional<String>?) {
        super.testDisabled(context, reason)
        updateStatus(TestResult.Disabled(context, reason))
    }
}
