package com.klarna.mobile.sdk.reactnative.klarnanetwork.payment

import com.facebook.react.BaseReactPackage
import com.facebook.react.bridge.NativeModule
import com.facebook.react.bridge.ReactApplicationContext
import com.facebook.react.module.model.ReactModuleInfo
import com.facebook.react.module.model.ReactModuleInfoProvider
import com.facebook.react.uimanager.ViewManager
import java.util.HashMap

class KlarnaNetworkPaymentPackage : BaseReactPackage() {
  override fun getModule(name: String, reactContext: ReactApplicationContext): NativeModule? =
    if (name == KlarnaNetworkPaymentModule.NAME) KlarnaNetworkPaymentModule(reactContext) else null

  override fun getReactModuleInfoProvider() = ReactModuleInfoProvider {
    mapOf(
      KlarnaNetworkPaymentModule.NAME to ReactModuleInfo(
        name = KlarnaNetworkPaymentModule.NAME,
        className = KlarnaNetworkPaymentModule.NAME,
        canOverrideExistingModule = false,
        needsEagerInit = false,
        isCxxModule = false,
        isTurboModule = true
      )
    )
  }

  override fun createViewManagers(reactContext: ReactApplicationContext): List<ViewManager<*, *>> =
    listOf(KlarnaPaymentButtonViewManager())
}
