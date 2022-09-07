package com.testapp.suite.payments

import com.testapp.tests.payments.flows.sliceit.SliceItUKFailureTest
import com.testapp.tests.payments.flows.sliceit.SliceItUKSuccessTest
import org.junit.jupiter.api.parallel.Execution
import org.junit.jupiter.api.parallel.ExecutionMode
import org.junit.platform.suite.api.SelectClasses
import org.junit.platform.suite.api.Suite

@Execution(ExecutionMode.CONCURRENT)
@Suite
@SelectClasses(
    SliceItUKSuccessTest::class,
    SliceItUKFailureTest::class
)
internal class SliceItPaySuite