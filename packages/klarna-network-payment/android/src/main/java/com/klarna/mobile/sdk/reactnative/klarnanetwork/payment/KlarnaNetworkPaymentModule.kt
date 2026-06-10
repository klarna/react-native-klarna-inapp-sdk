package com.klarna.mobile.sdk.reactnative.klarnanetwork.payment

import com.facebook.react.bridge.Promise
import com.facebook.react.bridge.ReactApplicationContext
import com.facebook.react.module.annotations.ReactModule
import com.klarna.mobile.sdk.reactnative.klarnanetwork.payment.NativeKlarnaNetworkPaymentSpec
import com.facebook.react.bridge.ReadableMap

@ReactModule(name = KlarnaNetworkPaymentModule.NAME)
class KlarnaNetworkPaymentModule(reactContext: ReactApplicationContext) :
    NativeKlarnaNetworkPaymentSpec(reactContext) {

    private val impl = KlarnaNetworkPaymentModuleImpl(reactContext)

    companion object {
        const val NAME = NativeKlarnaNetworkPaymentSpec.NAME
    }

    override fun initiateWithId(instanceId: String, paymentRequestId: String, promise: Promise?) {
        impl.initiateWithId(instanceId, paymentRequestId, promise)
    }

    override fun initiateWithData(instanceId: String, data: ReadableMap?, promise: Promise?) {
        impl.initiateWithData(instanceId, data, promise)
    }

    override fun fetch(instanceId: String, paymentRequestId: String, promise: Promise?) {
        impl.fetch(instanceId, paymentRequestId, promise)
    }

    override fun cancel(instanceId: String, paymentRequestId: String, promise: Promise?) {
        impl.cancel(instanceId, paymentRequestId, promise)
    }

    override fun presentationFetch(instanceId: String, data: ReadableMap?, promise: Promise?) {
        impl.presentationFetch(instanceId, data, promise)
    }

    override fun presentationHandleLink(instanceId: String, url: String, promise: Promise?) {
        impl.presentationHandleLink(instanceId, url, promise)
    }

    override fun dispose(instanceId: String, promise: Promise?) {
        impl.dispose(instanceId, promise)
    }
}
