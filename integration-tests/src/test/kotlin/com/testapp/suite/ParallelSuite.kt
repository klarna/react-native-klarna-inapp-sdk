package com.testapp.suite

import org.junit.runners.Suite
import org.junit.runners.model.RunnerBuilder
import org.junit.runners.model.RunnerScheduler
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit

class ParallelSuite(klass: Class<*>?, builder: RunnerBuilder) : Suite(klass, builder) {

    private val threadCount = 4

    init {
        setScheduler(object : RunnerScheduler {
            private val service: ExecutorService = Executors.newFixedThreadPool(threadCount)
            override fun schedule(childStatement: Runnable?) {
                childStatement?.let {
                    service.submit(it)
                }
            }

            override fun finished() {
                try {
                    service.shutdown()
                    service.awaitTermination(Long.MAX_VALUE, TimeUnit.NANOSECONDS)
                } catch (e: InterruptedException) {
                    e.printStackTrace(System.err)
                }
            }
        })
    }
}