package com.testapp.junit.extension.testwatcher

import org.junit.jupiter.api.extension.ExtensionContext
import org.junit.jupiter.api.extension.TestWatcher
import java.util.*

class TestWatcherProxy(
    private val successCallback: (() -> Unit)? = null,
    private val failureCallback: ((String) -> Unit)? = null,
    private val finishCallback: (() -> Unit)? = null
) : TestWatcher {

    override fun testSuccessful(context: ExtensionContext?) {
        successCallback?.invoke()
        finishCallback?.invoke()
        super.testSuccessful(context)
    }

    override fun testFailed(context: ExtensionContext?, cause: Throwable?) {
        context?.displayName?.let {
            failureCallback?.invoke(it)
        }
        finishCallback?.invoke()
        super.testFailed(context, cause)
    }

    override fun testAborted(context: ExtensionContext?, cause: Throwable?) {
        finishCallback?.invoke()
        super.testAborted(context, cause)
    }

    override fun testDisabled(context: ExtensionContext?, reason: Optional<String>?) {
        finishCallback?.invoke()
        super.testDisabled(context, reason)
    }
}
