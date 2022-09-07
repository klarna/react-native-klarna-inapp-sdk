package com.testapp.junit.extension.driver

import com.testapp.junit.getDriver
import org.junit.jupiter.api.extension.AfterAllCallback
import org.junit.jupiter.api.extension.ExtensionContext

class DriverQuitAfterAll : AfterAllCallback {
    override fun afterAll(context: ExtensionContext?) {
        context?.getDriver()?.quit()
    }
}
