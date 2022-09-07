package com.testapp.junit

import org.junit.jupiter.api.extension.ExtensionContext
import java.util.Optional

sealed class TestResult(val context: ExtensionContext?) {
    class Successful(context: ExtensionContext?) : TestResult(context)
    class Failed(context: ExtensionContext?, val cause: Throwable?) : TestResult(context)
    class Aborted(context: ExtensionContext?, val cause: Throwable?) : TestResult(context)
    class Disabled(context: ExtensionContext?, val reason: Optional<String>?) : TestResult(context)
}
