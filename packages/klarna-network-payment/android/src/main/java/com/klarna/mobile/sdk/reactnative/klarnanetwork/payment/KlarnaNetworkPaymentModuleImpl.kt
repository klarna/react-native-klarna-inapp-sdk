package com.klarna.mobile.sdk.reactnative.klarnanetwork.payment

import android.os.Handler
import android.os.Looper
import com.facebook.react.bridge.Promise
import com.facebook.react.bridge.ReactApplicationContext
import com.facebook.react.bridge.ReadableMap
import com.klarna.mobile.sdk.klarna.network.core.api.common.KlarnaResult
import com.klarna.mobile.sdk.klarna.network.payment.api.presentation.models.KlarnaPaymentPresentationContent
import com.klarna.mobile.sdk.klarna.network.payment.api.payment
import com.klarna.mobile.sdk.reactnative.klarnanetwork.core.KlarnaNetworkCoreModuleImpl
import java.util.concurrent.ConcurrentHashMap

class KlarnaNetworkPaymentModuleImpl(
    private val reactContext: ReactApplicationContext
) {
    companion object {
        const val NAME = "KlarnaNetworkPayment"
        private const val ERROR_ACTIVITY_NOT_FOUND = "Main Activity not found."
        private const val ERROR_INSTANCE_NOT_FOUND = "No instance found for the given instanceId. Call initialize first."
        private const val ERROR_NO_PRESENTATION_CONTENT = "No presentation content found. Call presentationFetch first."
        val storedPresentationContent: ConcurrentHashMap<String, KlarnaPaymentPresentationContent> = ConcurrentHashMap()
    }

    fun initiateWithId(instanceId: String, paymentRequestId: String, promise: Promise?) {
        val sdk = KlarnaNetworkCoreModuleImpl.getInstance(instanceId) ?: run {
            promise?.reject(NAME, ERROR_INSTANCE_NOT_FOUND)
            return
        }
        val activity = reactContext.currentActivity ?: run {
            promise?.reject(NAME, ERROR_ACTIVITY_NOT_FOUND)
            return
        }
        // PaymentControllerImpl calls fragmentManager.setFragmentResultListener() synchronously
        // inside initiate(), which calls lifecycle.addObserver() — must run on the main thread.
        Handler(Looper.getMainLooper()).post {
            sdk.payment.initiate(activity = activity, paymentRequestId = paymentRequestId) { result ->
                when (result) {
                    is KlarnaResult.Success -> promise?.resolve(result.value.toWritableMap())
                    is KlarnaResult.Failure -> promise?.reject(result.error.name, result.error.message)
                }
            }
        }
    }

    fun initiateWithData(instanceId: String, data: ReadableMap?, promise: Promise?) {
        val sdk = KlarnaNetworkCoreModuleImpl.getInstance(instanceId) ?: run {
            promise?.reject(NAME, ERROR_INSTANCE_NOT_FOUND)
            return
        }
        val activity = reactContext.currentActivity ?: run {
            promise?.reject(NAME, ERROR_ACTIVITY_NOT_FOUND)
            return
        }
        val paymentRequestData = try {
            data?.let { KlarnaPaymentRequestMapper.buildPaymentRequestData(it) }
        } catch (e: Exception) {
            promise?.reject(NAME, e.message ?: "Invalid payment request data")
            return
        } ?: run {
            promise?.reject(NAME, "data is required")
            return
        }
        // Same as initiateWithId — must run on the main thread for the same reason.
        Handler(Looper.getMainLooper()).post {
            sdk.payment.initiate(activity = activity, paymentRequestData = paymentRequestData) { result ->
                when (result) {
                    is KlarnaResult.Success -> promise?.resolve(result.value.toWritableMap())
                    is KlarnaResult.Failure -> promise?.reject(result.error.name, result.error.message)
                }
            }
        }
    }

    // No Handler wrap needed: fetch and cancel dispatch to Dispatchers.Main internally.
    fun fetch(instanceId: String, paymentRequestId: String, promise: Promise?) {
        val sdk = KlarnaNetworkCoreModuleImpl.getInstance(instanceId) ?: run {
            promise?.reject(NAME, ERROR_INSTANCE_NOT_FOUND)
            return
        }
        sdk.payment.fetch(paymentRequestId = paymentRequestId) { result ->
            when (result) {
                is KlarnaResult.Success -> promise?.resolve(result.value.toWritableMap())
                is KlarnaResult.Failure -> promise?.reject(result.error.name, result.error.message)
            }
        }
    }

    fun cancel(instanceId: String, paymentRequestId: String, promise: Promise?) {
        val sdk = KlarnaNetworkCoreModuleImpl.getInstance(instanceId) ?: run {
            promise?.reject(NAME, ERROR_INSTANCE_NOT_FOUND)
            return
        }
        sdk.payment.cancel(paymentRequestId = paymentRequestId) { result ->
            when (result) {
                is KlarnaResult.Success -> promise?.resolve(result.value.toWritableMap())
                is KlarnaResult.Failure -> promise?.reject(result.error.name, result.error.message)
            }
        }
    }

    // No Handler wrap needed: fetch dispatches to Dispatchers.Main internally.
    fun presentationFetch(instanceId: String, data: ReadableMap?, promise: Promise?) {
        val sdk = KlarnaNetworkCoreModuleImpl.getInstance(instanceId) ?: run {
            promise?.reject(NAME, ERROR_INSTANCE_NOT_FOUND)
            return
        }
        val presentationData = try {
            data?.let { KlarnaPaymentRequestMapper.buildPresentationData(it) } ?: run {
                promise?.reject(NAME, "data is required")
                return
            }
        } catch (e: Exception) {
            promise?.reject(NAME, e.message ?: "Invalid presentation data")
            return
        }
        sdk.payment.presentation.fetch(data = presentationData) { result ->
            when (result) {
                is KlarnaResult.Success -> {
                    if (KlarnaNetworkCoreModuleImpl.getInstance(instanceId) != null) {
                        storedPresentationContent[instanceId] = result.value
                    }
                    promise?.resolve(result.value.toWritableMap())
                }
                is KlarnaResult.Failure -> promise?.reject(result.error.name, result.error.message)
            }
        }
    }

    fun presentationHandleLink(instanceId: String, url: String, promise: Promise?) {
        val sdk = KlarnaNetworkCoreModuleImpl.getInstance(instanceId) ?: run {
            promise?.reject(NAME, ERROR_INSTANCE_NOT_FOUND)
            return
        }
        val activity = reactContext.currentActivity ?: run {
            promise?.reject(NAME, ERROR_ACTIVITY_NOT_FOUND)
            return
        }
        val content = storedPresentationContent[instanceId] ?: run {
            promise?.reject(NAME, ERROR_NO_PRESENTATION_CONTENT)
            return
        }
        // handleLink opens a browser/web flow and must run on the main thread.
        Handler(Looper.getMainLooper()).post {
            sdk.payment.presentation.handleLink(activity = activity, content = content, url = url) { result ->
                when (result) {
                    is KlarnaResult.Success -> {
                        if (KlarnaNetworkCoreModuleImpl.getInstance(instanceId) != null) {
                            storedPresentationContent[instanceId] = result.value
                        }
                        promise?.resolve(result.value.toWritableMap())
                    }
                    is KlarnaResult.Failure -> promise?.reject(result.error.name, result.error.message)
                }
            }
        }
    }

    fun dispose(instanceId: String, promise: Promise?) {
        storedPresentationContent.remove(instanceId)
        promise?.resolve(null)
    }
}
