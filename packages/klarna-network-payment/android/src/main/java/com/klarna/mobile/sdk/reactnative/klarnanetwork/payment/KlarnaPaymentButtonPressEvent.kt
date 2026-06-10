package com.klarna.mobile.sdk.reactnative.klarnanetwork.payment

import com.facebook.react.bridge.Arguments
import com.facebook.react.bridge.WritableMap
import com.facebook.react.uimanager.events.Event

internal class KlarnaPaymentButtonPressEvent(surfaceId: Int, viewId: Int) :
    Event<KlarnaPaymentButtonPressEvent>(surfaceId, viewId) {

    override fun getEventName(): String = EVENT_NAME

    override fun canCoalesce(): Boolean = false

    override fun getEventData(): WritableMap = Arguments.createMap()

    companion object {
        const val EVENT_NAME = "topButtonPress"
    }
}
