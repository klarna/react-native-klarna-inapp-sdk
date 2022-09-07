package com.testapp.junit.extension.driver

import com.testapp.junit.getDriver
import org.junit.jupiter.api.extension.ExtensionContext
import org.junit.jupiter.api.extension.TestWatcher
import java.util.*

class DriverQuitAfterFinished : TestWatcher {

    override fun testSuccessful(context: ExtensionContext?) {
        super.testSuccessful(context)
        context?.getDriver()?.quit()
    }

    override fun testFailed(context: ExtensionContext?, cause: Throwable?) {
        super.testFailed(context, cause)
        context?.getDriver()?.quit()
    }

    override fun testAborted(context: ExtensionContext?, cause: Throwable?) {
        super.testAborted(context, cause)
        context?.getDriver()?.quit()
    }

    override fun testDisabled(context: ExtensionContext?, reason: Optional<String>?) {
        super.testDisabled(context, reason)
        context?.getDriver()?.quit()
    }
}
