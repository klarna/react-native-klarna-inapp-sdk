package com.klarna.mobile.sdk.reactnative.klarnanetwork.messaging

import com.facebook.react.BaseReactPackage
import com.facebook.react.bridge.NativeModule
import com.facebook.react.bridge.ReactApplicationContext
import com.facebook.react.module.model.ReactModuleInfo
import com.facebook.react.module.model.ReactModuleInfoProvider
import com.facebook.react.uimanager.ViewManager

class KlarnaNetworkMessagingPackage : BaseReactPackage() {

    override fun getModule(name: String, reactContext: ReactApplicationContext): NativeModule? =
        if (name == KlarnaNetworkMessagingModule.NAME) KlarnaNetworkMessagingModule(reactContext) else null

    override fun getReactModuleInfoProvider(): ReactModuleInfoProvider =
        ReactModuleInfoProvider {
            mapOf(
                KlarnaNetworkMessagingModule.NAME to ReactModuleInfo(
                    name = KlarnaNetworkMessagingModule.NAME,
                    className = KlarnaNetworkMessagingModule.NAME,
                    canOverrideExistingModule = false,
                    needsEagerInit = false,
                    isCxxModule = false,
                    isTurboModule = true
                )
            )
        }

    override fun createViewManagers(reactContext: ReactApplicationContext): List<ViewManager<*, *>> =
        listOf<ViewManager<*, *>>(KlarnaMessagingPlacementViewManager())
}
