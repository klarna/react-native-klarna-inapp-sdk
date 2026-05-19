package com.klarna.mobile.sdk.reactnative.klarnanetwork.core

import com.facebook.react.bridge.Promise
import com.facebook.react.bridge.ReactApplicationContext
import com.facebook.fbreact.specs.NativeKlarnaNetworkCoreSpec
import com.facebook.react.bridge.ReadableMap
import com.facebook.react.module.annotations.ReactModule

@ReactModule(name = KlarnaNetworkCoreModule.NAME)
class KlarnaNetworkCoreModule(reactContext: ReactApplicationContext) :
  NativeKlarnaNetworkCoreSpec(reactContext) {
    companion object {
        const val NAME = NativeKlarnaNetworkCoreSpec.NAME
    }

    private val impl = KlarnaNetworkCoreModuleImpl(reactContext)

    override fun initialize(instanceId: String, configuration: ReadableMap?, promise: Promise?) {
        impl.initialize(instanceId, configuration, promise)
    }

    override fun getSessionToken(instanceId: String, promise: Promise?) {
        impl.getSessionToken(instanceId, promise)
    }

    override fun clearSession(instanceId: String, promise: Promise?) {
        impl.clearSession(instanceId, promise)
    }

    override fun handleReturnUrl(url: String?, promise: Promise?) {
        impl.handleReturnUrl(url, promise)
    }

    override fun setIntegrationMetadata(instanceId: String, metadata: ReadableMap?) {
        impl.setIntegrationMetadata(instanceId, metadata)
    }

    override fun dispose(instanceId: String, promise: Promise?) {
        impl.dispose(instanceId, promise)
    }
}
