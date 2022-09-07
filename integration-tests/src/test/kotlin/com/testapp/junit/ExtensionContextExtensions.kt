package com.testapp.junit

import com.testapp.annotation.DriverField
import com.testapp.extensions.tryOptional
import io.appium.java_client.AppiumDriver
import io.appium.java_client.android.AndroidDriver
import org.junit.jupiter.api.extension.ExtensionContext
import kotlin.reflect.full.memberProperties
import kotlin.reflect.jvm.javaField

fun ExtensionContext.getDriver(): AndroidDriver<*>? {
    val testInstances = this.requiredTestInstances?.allInstances
    testInstances?.forEach { instance ->
        val driverField = tryOptional {
            instance::class.memberProperties.first {
                it.javaField?.annotations?.filterIsInstance<DriverField>()?.isNotEmpty() == true
            }.javaField
        }
        val driver = driverField?.get(instance)
        return driver as? AndroidDriver<*>
    }
    return null
}

fun ExtensionContext.setDriver(driver: AppiumDriver<*>) {
    val testInstances = this.requiredTestInstances?.allInstances
    testInstances?.forEach { instance ->
        val driverField = tryOptional {
            instance::class.memberProperties.first {
                it.javaField?.annotations?.filterIsInstance<DriverField>()?.isNotEmpty() == true
            }.javaField
        }
        driverField?.set(instance, driver)
    }
}
