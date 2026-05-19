package com.klarna.mobile.sdk.reactnative.klarnanetwork.core

import com.facebook.react.BaseReactPackage
import com.facebook.react.bridge.NativeModule
import com.facebook.react.bridge.ReactApplicationContext
import com.facebook.react.module.model.ReactModuleInfo
import com.facebook.react.module.model.ReactModuleInfoProvider
import java.util.HashMap

class KlarnaNetworkCorePackage : BaseReactPackage() {
  override fun getModule(name: String, reactContext: ReactApplicationContext): NativeModule? {
    return if (name == KlarnaNetworkCoreModule.NAME) {
      KlarnaNetworkCoreModule(reactContext)
    } else {
      null
    }
  }

  override fun getReactModuleInfoProvider() = ReactModuleInfoProvider {
    mapOf(
      KlarnaNetworkCoreModule.NAME to ReactModuleInfo(
        name = KlarnaNetworkCoreModule.NAME,
        className = KlarnaNetworkCoreModule.NAME,
        canOverrideExistingModule = false,
        needsEagerInit = false,
        isCxxModule = false,
        isTurboModule = true
      )
    )
  }
}
