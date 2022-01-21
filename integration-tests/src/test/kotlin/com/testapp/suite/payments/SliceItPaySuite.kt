package com.testapp.suite.payments

import com.testapp.suite.ParallelSuite
import com.testapp.tests.payments.flows.sliceit.SliceItUKFailureTest
import com.testapp.tests.payments.flows.sliceit.SliceItUKSuccessTest
import org.junit.runner.RunWith
import org.junit.runners.Suite

@RunWith(ParallelSuite::class)
@Suite.SuiteClasses(
        SliceItUKSuccessTest::class,
        SliceItUKFailureTest::class
)
internal class SliceItPaySuite