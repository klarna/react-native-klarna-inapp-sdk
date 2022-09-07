package com.testapp.suite.payments

import com.testapp.tests.payments.flows.paynow.PayNowSofortFailureTest
import com.testapp.tests.payments.flows.paynow.PayNowSofortSuccessTest
import org.junit.jupiter.api.parallel.Execution
import org.junit.jupiter.api.parallel.ExecutionMode
import org.junit.platform.suite.api.SelectClasses
import org.junit.platform.suite.api.Suite

@Execution(ExecutionMode.CONCURRENT)
@Suite
@SelectClasses(
    PayNowSofortSuccessTest::class,
    PayNowSofortFailureTest::class
)
internal class PayNowTestSuite