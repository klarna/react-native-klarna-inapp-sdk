package com.klarna.mobile.sdk.reactnative.klarnanetwork.messaging

import com.facebook.react.bridge.Arguments
import com.facebook.react.bridge.WritableMap
import com.facebook.react.uimanager.events.Event

class KlarnaMessagingPlacementErrorEvent(
    surfaceId: Int,
    viewTag: Int,
    private val errorName: String,
    private val errorMessage: String
) : Event<KlarnaMessagingPlacementErrorEvent>(surfaceId, viewTag) {

    override fun getEventName(): String = "onError"

    override fun getEventData(): WritableMap {
        val errorMap = Arguments.createMap()
        errorMap.putString("name", errorName)
        errorMap.putString("message", errorMessage)

        val data = Arguments.createMap()
        data.putMap("error", errorMap)
        return data
    }
}
