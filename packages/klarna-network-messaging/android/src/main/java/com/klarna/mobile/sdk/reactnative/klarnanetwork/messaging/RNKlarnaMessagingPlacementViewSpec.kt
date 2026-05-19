package com.klarna.mobile.sdk.reactnative.klarnanetwork.messaging

import android.view.View
import com.facebook.react.bridge.ReadableArray
import com.facebook.react.uimanager.SimpleViewManager
import com.facebook.react.uimanager.ViewManagerDelegate
import com.facebook.react.viewmanagers.RNKlarnaMessagingPlacementViewManagerDelegate
import com.facebook.react.viewmanagers.RNKlarnaMessagingPlacementViewManagerInterface

abstract class RNKlarnaMessagingPlacementViewSpec<T : View> :
    SimpleViewManager<T>(),
    RNKlarnaMessagingPlacementViewManagerInterface<T> {

    private val delegate: ViewManagerDelegate<T> =
        RNKlarnaMessagingPlacementViewManagerDelegate(this)

    override fun getDelegate(): ViewManagerDelegate<T> = delegate

    override fun receiveCommand(root: T, commandId: String, args: ReadableArray) {
        delegate.receiveCommand(root, commandId, args)
    }
}
