package com.klarna.mobile.sdk.reactnative.klarnanetwork.payment

import android.view.View
import com.facebook.react.bridge.ReadableArray
import com.facebook.react.uimanager.SimpleViewManager
import com.facebook.react.uimanager.ViewManagerDelegate
import com.facebook.react.viewmanagers.KlarnaNetworkPaymentButtonManagerDelegate
import com.facebook.react.viewmanagers.KlarnaNetworkPaymentButtonManagerInterface

abstract class KlarnaNetworkPaymentButtonViewSpec<T : View> :
    SimpleViewManager<T>(),
    KlarnaNetworkPaymentButtonManagerInterface<T> {

    private val delegate: ViewManagerDelegate<T> =
        KlarnaNetworkPaymentButtonManagerDelegate(this)

    override fun getDelegate(): ViewManagerDelegate<T> = delegate

    override fun receiveCommand(root: T, commandId: String, args: ReadableArray) {
        delegate.receiveCommand(root, commandId, args)
    }
}
