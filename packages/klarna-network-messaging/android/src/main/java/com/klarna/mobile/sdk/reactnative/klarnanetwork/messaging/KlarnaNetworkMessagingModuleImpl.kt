package com.klarna.mobile.sdk.reactnative.klarnanetwork.messaging

import android.os.Handler
import android.os.Looper
import com.facebook.react.bridge.Promise
import com.facebook.react.bridge.ReactApplicationContext
import com.facebook.react.bridge.ReadableMap
import com.klarna.mobile.sdk.klarna.network.core.api.common.KlarnaResult
import com.klarna.mobile.sdk.klarna.network.messaging.api.messaging
import com.klarna.mobile.sdk.reactnative.klarnanetwork.core.KlarnaNetworkCoreModuleImpl

class KlarnaNetworkMessagingModuleImpl(
    private val reactContext: ReactApplicationContext
) {
    companion object {
        const val NAME = "KlarnaNetworkMessaging"
        private const val ERROR_ACTIVITY_NOT_FOUND = "Main Activity not found."
        private const val ERROR_INSTANCE_NOT_FOUND = "No instance found for the given instanceId. Call initialize first."
    }

    fun initiatePrequalification(instanceId: String, data: ReadableMap?, promise: Promise?) {
        val sdk = KlarnaNetworkCoreModuleImpl.getInstance(instanceId) ?: run {
            promise?.reject(NAME, ERROR_INSTANCE_NOT_FOUND)
            return
        }
        val activity = reactContext.currentActivity ?: run {
            promise?.reject(NAME, ERROR_ACTIVITY_NOT_FOUND)
            return
        }
        val prequalificationData = try {
            KlarnaMessagingPrequalificationMapper.buildPrequalificationData(data)
        } catch (e: IllegalArgumentException) {
            promise?.reject(NAME, e.message)
            return
        }
        // The prequalification controller registers lifecycle observers and presents the flow
        // synchronously inside initiate(), so it must run on the main thread.
        Handler(Looper.getMainLooper()).post {
            sdk.messaging.prequalification.initiate(activity, prequalificationData) { result ->
                when (result) {
                    is KlarnaResult.Success -> promise?.resolve(
                        KlarnaMessagingPrequalificationMapper.toWritableMap(result.value)
                    )
                    is KlarnaResult.Failure -> promise?.reject(result.error.name, result.error.message)
                }
            }
        }
    }

    // No state is kept per instance, so there is nothing to release.
    fun dispose(instanceId: String, promise: Promise?) {
        promise?.resolve(null)
    }
}
