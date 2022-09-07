package com.testapp.junit.extension.testwatcher.browserstack

import com.testapp.extensions.setTestStatus
import com.testapp.junit.TestResult
import com.testapp.junit.getDriver
import org.junit.jupiter.api.extension.AfterAllCallback
import org.junit.jupiter.api.extension.ExtensionContext
import org.junit.jupiter.api.extension.TestWatcher
import java.util.*

class BrowserStackAllTestWatcher : TestWatcher, AfterAllCallback {
    private val testResults: MutableList<TestResult> = mutableListOf()

    private fun registerTestResult(testResult: TestResult) {
        testResults.add(testResult)
    }

    private fun updateStatusAll(context: ExtensionContext?) {
        val driver = context?.getDriver()
        val results = testResults.mapNotNull { testResult ->
            when (testResult) {
                is TestResult.Successful -> null
                is TestResult.Failed -> testResult.context?.displayName
                is TestResult.Aborted -> testResult.context?.displayName
                is TestResult.Disabled -> null
            }
        }
        if (results.isEmpty()) {
            driver?.setTestStatus(true)
        } else {
            val resultMessage = results.joinToString(", ")
            driver?.setTestStatus(false, resultMessage)
        }
    }

    override fun testSuccessful(context: ExtensionContext?) {
        super.testSuccessful(context)
        registerTestResult(TestResult.Successful(context))
    }

    override fun testFailed(context: ExtensionContext?, cause: Throwable?) {
        super.testFailed(context, cause)
        registerTestResult(TestResult.Failed(context, cause))
    }

    override fun testAborted(context: ExtensionContext?, cause: Throwable?) {
        super.testAborted(context, cause)
        registerTestResult(TestResult.Aborted(context, cause))
    }

    override fun testDisabled(context: ExtensionContext?, reason: Optional<String>?) {
        super.testDisabled(context, reason)
        registerTestResult(TestResult.Disabled(context, reason))
    }

    override fun afterAll(context: ExtensionContext?) {
        updateStatusAll(context)
    }
}
