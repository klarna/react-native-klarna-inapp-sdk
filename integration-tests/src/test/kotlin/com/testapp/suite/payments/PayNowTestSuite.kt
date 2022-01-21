package com.testapp.suite.payments

import com.testapp.suite.ParallelSuite
import com.testapp.tests.payments.flows.paynow.PayNowSofortFailureTest
import com.testapp.tests.payments.flows.paynow.PayNowSofortSuccessTest
import org.junit.runner.RunWith
import org.junit.runners.Suite

@RunWith(ParallelSuite::class)
@Suite.SuiteClasses(
        PayNowSofortSuccessTest::class,
        PayNowSofortFailureTest::class
)
internal class PayNowTestSuite