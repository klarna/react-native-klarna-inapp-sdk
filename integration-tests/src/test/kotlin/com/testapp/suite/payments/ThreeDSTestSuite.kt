package com.testapp.suite.payments

import com.testapp.tests.payments.flows.threeds.ThreeDSFailureTest
import com.testapp.tests.payments.flows.threeds.ThreeDSSuccessTest
import org.junit.jupiter.api.parallel.Execution
import org.junit.jupiter.api.parallel.ExecutionMode
import org.junit.platform.suite.api.SelectClasses
import org.junit.platform.suite.api.Suite

@Execution(ExecutionMode.CONCURRENT)
@Suite
@SelectClasses(
    ThreeDSSuccessTest::class,
    ThreeDSFailureTest::class
)
internal class ThreeDSTestSuite