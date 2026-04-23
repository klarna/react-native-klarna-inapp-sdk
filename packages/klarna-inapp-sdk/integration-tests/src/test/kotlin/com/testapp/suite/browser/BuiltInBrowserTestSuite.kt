package com.testapp.suite.browser

import com.testapp.tests.browser.BuiltInBrowserPayLaterTest
import com.testapp.tests.browser.BuiltInBrowserPayNowTest
import com.testapp.tests.browser.BuiltInBrowserSliceItTest
import org.junit.jupiter.api.parallel.Execution
import org.junit.jupiter.api.parallel.ExecutionMode
import org.junit.platform.suite.api.SelectClasses
import org.junit.platform.suite.api.Suite

@Execution(ExecutionMode.CONCURRENT)
@Suite
@SelectClasses(
    BuiltInBrowserPayNowTest::class,
    BuiltInBrowserPayLaterTest::class,
    BuiltInBrowserSliceItTest::class
)
internal class BuiltInBrowserTestSuite