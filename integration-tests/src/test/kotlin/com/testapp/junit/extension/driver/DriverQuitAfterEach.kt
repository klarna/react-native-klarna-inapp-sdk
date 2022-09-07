package com.testapp.junit.extension.driver

import com.testapp.junit.getDriver
import org.junit.jupiter.api.extension.AfterEachCallback
import org.junit.jupiter.api.extension.ExtensionContext

class DriverQuitAfterEach : AfterEachCallback {
    override fun afterEach(context: ExtensionContext?) {
        context?.getDriver()?.quit()
    }
}
