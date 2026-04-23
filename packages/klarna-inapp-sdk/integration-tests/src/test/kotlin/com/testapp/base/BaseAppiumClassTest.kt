package com.testapp.base

import com.testapp.junit.extension.TimingExtension
import com.testapp.annotation.DriverField
import com.testapp.junit.extension.driver.DriverQuitAfterAll
import com.testapp.junit.extension.driver.DriverResetBeforeEach
import com.testapp.junit.extension.driver.DriverSetupBeforeAll
import com.testapp.junit.extension.testwatcher.LoggerTestWatcher
import com.testapp.junit.extension.testwatcher.browserstack.BrowserStackAllTestWatcher
import io.appium.java_client.AppiumDriver
import io.appium.java_client.MobileElement
import org.junit.jupiter.api.TestInstance
import org.junit.jupiter.api.extension.ExtendWith

@ExtendWith(
    DriverSetupBeforeAll::class,
    TimingExtension::class,
    LoggerTestWatcher::class,
    DriverResetBeforeEach::class,
    DriverQuitAfterAll::class,
    BrowserStackAllTestWatcher::class
)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
internal open class BaseAppiumClassTest : BaseAppiumTest() {

    @DriverField
    lateinit var driver: AppiumDriver<MobileElement>
}
