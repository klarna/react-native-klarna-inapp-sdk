package com.klarna.mobile.sdk.reactnative.expresscheckout

import com.facebook.react.uimanager.events.EventDispatcher
import com.klarna.mobile.sdk.api.expresscheckout.KlarnaExpressCheckoutButtonAuthorizationResponse
import com.klarna.mobile.sdk.api.expresscheckout.KlarnaExpressCheckoutError
import com.klarna.mobile.sdk.reactnative.common.event.ComponentEventSender
import android.widget.FrameLayout
import com.klarna.mobile.sdk.reactnative.common.util.ArgumentsUtil

internal class KlarnaExpressCheckoutViewEventSender(
    viewToDispatcher: Map<FrameLayout, EventDispatcher>
) : ComponentEventSender<FrameLayout>(viewToDispatcher) {

    override fun getCallbackEventNames(): Collection<String> = listOf(
        EVENT_NAME_ON_AUTHORIZED,
        EVENT_NAME_ON_ERROR,
        EVENT_NAME_ON_RESIZED
    )

    fun sendOnAuthorizedEvent(
        view: FrameLayout,
        response: KlarnaExpressCheckoutButtonAuthorizationResponse
    ) {
        val responseMap = mapOf<String, Any>(
            "showForm" to response.showForm,
            "approved" to response.approved,
            "finalizedRequired" to response.finalizeRequired,
            "clientToken" to (response.clientToken ?: ""),
            "authorizationToken" to (response.authorizationToken ?: ""),
            "sessionId" to (response.sessionId ?: ""),
            "collectedShippingAddress" to (response.collectedShippingAddress ?: ""),
            "merchantReference1" to (response.merchantReference1 ?: ""),
            "merchantReference2" to (response.merchantReference2 ?: "")
        )
        val authorizationResponseMap = ArgumentsUtil.createMap(responseMap)
        val params = ArgumentsUtil.createMap(
            mapOf<String, Any>("authorizationResponse" to authorizationResponseMap)
        )
        postEventForView(view, EVENT_NAME_ON_AUTHORIZED, params)
    }

    fun sendKlarnaExpressCheckoutError(
        view: FrameLayout,
        error: KlarnaExpressCheckoutError
    ) {
        sendKlarnaExpressCheckoutError(view, error.name, error.message, error.isFatal)
    }

    fun sendKlarnaExpressCheckoutError(
        view: FrameLayout,
        name: String,
        message: String,
        isFatal: Boolean
    ) {
        val errorMap = ArgumentsUtil.createMap(
            mapOf<String, Any>(
                "name" to name,
                "message" to message,
                "isFatal" to isFatal
            )
        )
        val params = ArgumentsUtil.createMap(
            mapOf<String, Any>("error" to errorMap)
        )
        postEventForView(view, EVENT_NAME_ON_ERROR, params)
    }

    fun sendOnResizedEvent(view: FrameLayout, height: Int) {
        val params = ArgumentsUtil.createMap(
            mapOf<String, Any>("height" to height.toString())
        )
        postEventForView(view, EVENT_NAME_ON_RESIZED, params)
    }

    companion object {
        private const val EVENT_NAME_ON_AUTHORIZED = "onAuthorized"
        private const val EVENT_NAME_ON_ERROR = "onError"
        private const val EVENT_NAME_ON_RESIZED = "onResized"
    }
}
