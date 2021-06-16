package com.testapp.suite

import com.testapp.tests.browser.BuiltInBrowserPayLaterTest
import com.testapp.tests.browser.BuiltInBrowserPayNowTest
import com.testapp.tests.browser.BuiltInBrowserSliceItTest
import com.testapp.tests.payments.flows.paylater.PayLaterDETest
import com.testapp.tests.payments.flows.paylater.PayLaterFITest
import com.testapp.tests.payments.flows.paylater.PayLaterNOTest
import com.testapp.tests.payments.flows.paylater.PayLaterSETest
import com.testapp.tests.payments.flows.paynow.PayNowSofortTest
import com.testapp.tests.payments.flows.sliceit.SliceItUKTest
import com.testapp.tests.payments.flows.threeds.`3DSTest`
import org.junit.runner.RunWith
import org.junit.runners.Suite

@RunWith(ParallelSuite::class)
@Suite.SuiteClasses(
    BuiltInBrowserPayNowTest::class,
    BuiltInBrowserPayLaterTest::class,
    BuiltInBrowserSliceItTest::class,
    PayLaterDETest::class,
    PayLaterFITest::class,
    PayLaterNOTest::class,
    PayLaterSETest::class,
    PayNowSofortTest::class,
    SliceItUKTest::class,
    `3DSTest`::class
)
internal class TestSuite
