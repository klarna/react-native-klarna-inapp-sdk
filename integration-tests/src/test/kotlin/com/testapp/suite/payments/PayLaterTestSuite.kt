package com.testapp.suite.payments

import com.testapp.suite.ParallelSuite
import com.testapp.tests.payments.flows.paylater.*
import org.junit.runner.RunWith
import org.junit.runners.Suite

@RunWith(ParallelSuite::class)
@Suite.SuiteClasses(
        PayLaterDESuccessTest::class,
        PayLaterDEFailureTest::class,
        PayLaterFISuccessTest::class,
        PayLaterFIFailureTest::class,
        PayLaterNOSuccessTest::class,
        PayLaterNOFailureTest::class,
        PayLaterSESuccessTest::class,
        PayLaterSEFailureTest::class
)
internal class PayLaterTestSuite