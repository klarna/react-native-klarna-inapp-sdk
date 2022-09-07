package com.testapp.junit.extension.driver

import com.testapp.base.BaseAppiumTest
import com.testapp.junit.setDriver
import org.junit.jupiter.api.extension.BeforeEachCallback
import org.junit.jupiter.api.extension.ExtensionContext

class DriverSetupBeforeEach : BeforeEachCallback {
    override fun beforeEach(context: ExtensionContext?) {
        context?.setDriver(BaseAppiumTest.setupDriver(context.displayName))
    }
}
