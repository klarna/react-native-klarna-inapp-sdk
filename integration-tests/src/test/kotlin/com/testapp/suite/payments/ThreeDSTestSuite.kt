package com.testapp.suite.payments

import com.testapp.suite.ParallelSuite
import com.testapp.tests.payments.flows.threeds.ThreeDSFailureTest
import com.testapp.tests.payments.flows.threeds.ThreeDSSuccessTest
import org.junit.runner.RunWith
import org.junit.runners.Suite

@RunWith(ParallelSuite::class)
@Suite.SuiteClasses(
        ThreeDSSuccessTest::class,
        ThreeDSFailureTest::class
)
internal class ThreeDSTestSuite