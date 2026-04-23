package com.testapp.junit.extension.testwatcher

import org.junit.jupiter.api.extension.*
import java.util.*
import java.util.logging.Logger

class LoggerTestWatcher : TestWatcher, BeforeAllCallback, BeforeEachCallback, AfterEachCallback, AfterAllCallback {

    companion object {
        private val logger: Logger = Logger.getLogger(LoggerTestWatcher::class.java.name)
    }

    override fun testSuccessful(context: ExtensionContext?) {
        super.testSuccessful(context)
        logger.info("Test Successful for test ${context?.displayName}")
    }

    override fun testFailed(context: ExtensionContext?, cause: Throwable?) {
        super.testFailed(context, cause)
        logger.info("Test Failed for test ${context?.displayName}: with cause : ${cause?.message}")
    }

    override fun testAborted(context: ExtensionContext?, cause: Throwable?) {
        super.testAborted(context, cause)
        logger.info("Test Aborted for test ${context?.displayName}: with cause : ${cause?.message}")
    }

    override fun testDisabled(context: ExtensionContext?, reason: Optional<String>?) {
        super.testDisabled(context, reason)
        logger.info("Test Disabled for test ${context?.displayName}: with reason : $reason")
    }

    override fun beforeAll(context: ExtensionContext?) {
        logger.info("Tests Starting -> ${context?.displayName}")
    }

    override fun beforeEach(context: ExtensionContext?) {
        logger.info("Test Starting -> ${context?.displayName}")
    }

    override fun afterEach(context: ExtensionContext?) {
        logger.info("Test Finished -> ${context?.displayName}")
    }

    override fun afterAll(context: ExtensionContext?) {
        logger.info("Tests Finished -> ${context?.displayName}")
    }
}
