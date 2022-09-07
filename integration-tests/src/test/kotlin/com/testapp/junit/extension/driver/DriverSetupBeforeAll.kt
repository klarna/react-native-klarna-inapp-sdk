package com.testapp.junit.extension.driver

import com.testapp.base.BaseAppiumTest
import com.testapp.junit.setDriver
import org.junit.jupiter.api.extension.BeforeAllCallback
import org.junit.jupiter.api.extension.ExtensionContext

class DriverSetupBeforeAll : BeforeAllCallback {
    override fun beforeAll(context: ExtensionContext?) {
        context?.setDriver(BaseAppiumTest.setupDriver(context.displayName))
    }
}
