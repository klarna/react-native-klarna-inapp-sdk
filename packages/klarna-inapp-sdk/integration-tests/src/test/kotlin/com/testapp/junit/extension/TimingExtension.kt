package com.testapp.junit.extension

import org.apache.commons.lang3.time.DurationFormatUtils
import org.junit.jupiter.api.extension.AfterTestExecutionCallback
import org.junit.jupiter.api.extension.BeforeTestExecutionCallback
import org.junit.jupiter.api.extension.ExtensionContext
import java.util.logging.Logger

// inspired by https://github.com/junit-team/junit5/blob/main/documentation/src/test/java/example/timing/TimingExtension.java
class TimingExtension : BeforeTestExecutionCallback, AfterTestExecutionCallback {

    @Throws(Exception::class)
    override fun beforeTestExecution(context: ExtensionContext) {
        getStore(context).put(START_TIME, System.currentTimeMillis())
    }

    @Throws(Exception::class)
    override fun afterTestExecution(context: ExtensionContext) {
        val currentMillis = System.currentTimeMillis()
        val testMethod = context.requiredTestMethod
        val startTime = getStore(context).remove(
            START_TIME,
            Long::class.javaPrimitiveType
        )
        val duration = currentMillis - startTime
        val durationString = DurationFormatUtils.formatDuration(duration, "HH:mm:ss.SSS")
        logger.info {
            String.format(
                "Method [%s] took %s (HH:mm:ss.SSS)",
                testMethod.name,
                durationString
            )
        }
    }

    private fun getStore(context: ExtensionContext): ExtensionContext.Store {
        return context.getStore(ExtensionContext.Namespace.create(javaClass, context.requiredTestMethod))
    }

    companion object {
        private val logger = Logger.getLogger(TimingExtension::class.java.name)
        private const val START_TIME = "start time"
    }
}
