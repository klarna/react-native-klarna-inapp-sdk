package com.testapp.suite

import com.testapp.tests.appium.TestAppiumServer
import com.testapp.tests.browser.TestBuiltInBrowser
import com.testapp.tests.payments.flows.paylater.TestPayLater
import com.testapp.tests.payments.flows.paynow.TestPayNowSofort
import com.testapp.tests.payments.flows.sliceit.TestSliceItUK
import com.testapp.tests.payments.flows.threeds.Test3DS
import org.junit.runner.RunWith
import org.junit.runners.Suite

@RunWith(ParallelSuite::class)
@Suite.SuiteClasses(
    TestAppiumServer::class,
    TestBuiltInBrowser::class,
    TestPayLater::class,
    TestPayNowSofort::class,
    TestSliceItUK::class,
    Test3DS::class
)
internal class TestSuite