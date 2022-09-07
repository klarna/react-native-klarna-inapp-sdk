package com.testapp.suite

import com.testapp.tests.browser.BuiltInBrowserPayLaterTest
import com.testapp.tests.browser.BuiltInBrowserPayNowTest
import com.testapp.tests.browser.BuiltInBrowserSliceItTest
import com.testapp.tests.payments.flows.paylater.*
import com.testapp.tests.payments.flows.paynow.PayNowSofortFailureTest
import com.testapp.tests.payments.flows.paynow.PayNowSofortSuccessTest
import com.testapp.tests.payments.flows.sliceit.SliceItUKFailureTest
import com.testapp.tests.payments.flows.sliceit.SliceItUKSuccessTest
import com.testapp.tests.payments.flows.threeds.ThreeDSFailureTest
import com.testapp.tests.payments.flows.threeds.ThreeDSSuccessTest
import org.junit.jupiter.api.parallel.Execution
import org.junit.jupiter.api.parallel.ExecutionMode
import org.junit.platform.suite.api.SelectClasses
import org.junit.platform.suite.api.Suite

@Execution(ExecutionMode.CONCURRENT)
@Suite
@SelectClasses(
    BuiltInBrowserPayNowTest::class,
    BuiltInBrowserPayLaterTest::class,
    BuiltInBrowserSliceItTest::class,
    PayLaterDESuccessTest::class,
    PayLaterDEFailureTest::class,
    PayLaterFISuccessTest::class,
    PayLaterFIFailureTest::class,
    PayLaterNOSuccessTest::class,
    PayLaterNOFailureTest::class,
    PayLaterSESuccessTest::class,
    PayLaterSEFailureTest::class,
    PayNowSofortSuccessTest::class,
    PayNowSofortFailureTest::class,
    SliceItUKSuccessTest::class,
    SliceItUKFailureTest::class,
    ThreeDSSuccessTest::class,
    ThreeDSFailureTest::class
)
internal class TestSuite
