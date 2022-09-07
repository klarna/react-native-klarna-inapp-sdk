package com.testapp.utils

import io.appium.java_client.service.local.AppiumDriverLocalService
import io.appium.java_client.service.local.AppiumServiceBuilder
import io.appium.java_client.service.local.flags.GeneralServerFlag

object ServiceUtil {

    private var appiumService: AppiumDriverLocalService? = null

    fun getAppiumService(): AppiumDriverLocalService {
        appiumService?.let {
            return it
        } ?: run {
            synchronized(this@ServiceUtil) {
                val service = AppiumServiceBuilder().withArgument(GeneralServerFlag.LOG_LEVEL, "error").build()
                    .apply {
                        start()
                    }
                appiumService = service
                return service
            }
        }
    }
}
