package com.klarna.mobile.sdk.reactnative.klarnanetwork.messaging

import com.facebook.react.bridge.Arguments
import com.facebook.react.bridge.WritableMap
import com.facebook.react.uimanager.events.Event

class KlarnaMessagingPlacementResizedEvent(
    surfaceId: Int,
    viewTag: Int,
    private val height: String
) : Event<KlarnaMessagingPlacementResizedEvent>(surfaceId, viewTag) {

    override fun getEventName(): String = "onResized"

    override fun getEventData(): WritableMap {
        val data = Arguments.createMap()
        data.putString("height", height)
        return data
    }
}
