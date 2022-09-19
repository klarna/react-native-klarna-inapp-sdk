package com.testapp.junit.extension.driver

import com.testapp.junit.TestUtil
import com.testapp.junit.getDriver
import org.junit.jupiter.api.extension.BeforeEachCallback
import org.junit.jupiter.api.extension.ExtensionContext

class DriverResetBeforeEach : BeforeEachCallback {
    override fun beforeEach(context: ExtensionContext) {
        val driver = context.getDriver()
        try {
            driver?.resetApp()
        } catch (t: Throwable) {
            TestUtil.assumeNoException(t)
        }
    }
}
