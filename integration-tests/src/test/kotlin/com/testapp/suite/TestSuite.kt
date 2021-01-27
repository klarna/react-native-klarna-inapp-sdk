package com.testapp.suite

import com.testapp.tests.appium.TestAppiumServer
import com.testapp.tests.browser.BuiltInBrowserPayLaterTest
import com.testapp.tests.browser.BuiltInBrowserPayNowTest
import com.testapp.tests.browser.BuiltInBrowserSliceItTest
import com.testapp.tests.payments.flows.paylater.PayLaterDETest
import com.testapp.tests.payments.flows.paylater.PayLaterFITest
import com.testapp.tests.payments.flows.paylater.PayLaterNOTest
import com.testapp.tests.payments.flows.paylater.PayLaterSETest
import com.testapp.tests.payments.flows.paynow.TestPayNowSofort
import com.testapp.tests.payments.flows.sliceit.TestSliceItUK
import com.testapp.tests.payments.flows.threeds.Test3DS
import org.junit.runner.RunWith
import org.junit.runners.Suite

@RunWith(ParallelSuite::class)
@Suite.SuiteClasses(
    TestAppiumServer::class,
    BuiltInBrowserPayNowTest::class,
    BuiltInBrowserPayLaterTest::class,
    BuiltInBrowserSliceItTest::class,
    PayLaterDETest::class,
    PayLaterFITest::class,
    PayLaterNOTest::class,
    PayLaterSETest::class,
    TestPayNowSofort::class,
    TestSliceItUK::class,
    Test3DS::class
)
internal class TestSuite