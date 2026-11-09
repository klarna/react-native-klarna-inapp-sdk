package com.klarna.mobile.sdk.reactnative.klarnanetwork.messaging

import com.facebook.react.bridge.Promise
import com.facebook.react.bridge.ReactApplicationContext
import com.facebook.react.bridge.ReadableMap
import com.facebook.react.module.annotations.ReactModule

@ReactModule(name = KlarnaNetworkMessagingModule.NAME)
class KlarnaNetworkMessagingModule(reactContext: ReactApplicationContext) :
    NativeKlarnaNetworkMessagingSpec(reactContext) {

    private val impl = KlarnaNetworkMessagingModuleImpl(reactContext)

    companion object {
        const val NAME = NativeKlarnaNetworkMessagingSpec.NAME
    }

    override fun initiatePrequalification(instanceId: String, data: ReadableMap?, promise: Promise?) {
        impl.initiatePrequalification(instanceId, data, promise)
    }

    override fun dispose(instanceId: String, promise: Promise?) {
        impl.dispose(instanceId, promise)
    }
}
