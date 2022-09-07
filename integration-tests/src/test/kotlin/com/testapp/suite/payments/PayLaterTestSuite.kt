package com.testapp.suite.payments

import com.testapp.tests.payments.flows.paylater.*
import org.junit.jupiter.api.parallel.Execution
import org.junit.jupiter.api.parallel.ExecutionMode
import org.junit.platform.suite.api.SelectClasses
import org.junit.platform.suite.api.Suite

@Execution(ExecutionMode.CONCURRENT)
@Suite
@SelectClasses(
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