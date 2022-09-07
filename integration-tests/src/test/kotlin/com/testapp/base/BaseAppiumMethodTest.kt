package com.testapp.base

import com.testapp.junit.extension.TimingExtension
import com.testapp.annotation.DriverField
import com.testapp.junit.extension.driver.DriverQuitAfterFinished
import com.testapp.junit.extension.driver.DriverSetupBeforeEach
import com.testapp.junit.extension.testwatcher.LoggerTestWatcher
import com.testapp.junit.extension.testwatcher.browserstack.BrowserStackEachTestWatcher
import io.appium.java_client.AppiumDriver
import io.appium.java_client.MobileElement
import org.junit.jupiter.api.TestInstance
import org.junit.jupiter.api.extension.ExtendWith

@ExtendWith(
    DriverSetupBeforeEach::class,
    TimingExtension::class,
    LoggerTestWatcher::class,
    DriverQuitAfterFinished::class,
    BrowserStackEachTestWatcher::class
)
@TestInstance(TestInstance.Lifecycle.PER_METHOD)
internal open class BaseAppiumMethodTest : BaseAppiumTest() {

    @DriverField
    lateinit var driver: AppiumDriver<MobileElement>
}
