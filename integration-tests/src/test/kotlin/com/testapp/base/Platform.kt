package com.testapp.base

import io.appium.java_client.remote.MobilePlatform

enum class Platform(val platformName: String) {
    ANDROID(MobilePlatform.ANDROID),
    IOS(MobilePlatform.IOS);

    companion object {
        fun getSystemConfiguration(): Platform {
            return if ("ios".equals(System.getProperty("platform"), ignoreCase = true)) {
                IOS
            } else {
                ANDROID
            }
        }
    }
}