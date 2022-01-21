package com.testapp.suite.browser

import com.testapp.suite.ParallelSuite
import com.testapp.tests.browser.BuiltInBrowserPayLaterTest
import com.testapp.tests.browser.BuiltInBrowserPayNowTest
import com.testapp.tests.browser.BuiltInBrowserSliceItTest
import org.junit.runner.RunWith
import org.junit.runners.Suite

@RunWith(ParallelSuite::class)
@Suite.SuiteClasses(
        BuiltInBrowserPayNowTest::class,
        BuiltInBrowserPayLaterTest::class,
        BuiltInBrowserSliceItTest::class
)
internal class BuiltInBrowserTestSuite