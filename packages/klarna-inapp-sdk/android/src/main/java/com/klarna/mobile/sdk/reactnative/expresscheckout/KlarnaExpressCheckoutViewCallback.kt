package com.klarna.mobile.sdk.reactnative.expresscheckout

import com.klarna.mobile.sdk.api.expresscheckout.KlarnaExpressCheckoutButton
import com.klarna.mobile.sdk.api.expresscheckout.KlarnaExpressCheckoutButtonAuthorizationResponse
import com.klarna.mobile.sdk.api.expresscheckout.KlarnaExpressCheckoutButtonCallback
import com.klarna.mobile.sdk.api.expresscheckout.KlarnaExpressCheckoutError
import android.widget.FrameLayout

/**
 * Implements the SDK's callback interface and forwards events to the React Native event sender.
 */
internal class KlarnaExpressCheckoutViewCallback(
    private val containerView: FrameLayout,
    private val eventSender: KlarnaExpressCheckoutViewEventSender
) : KlarnaExpressCheckoutButtonCallback {

    override fun onAuthorized(
        view: KlarnaExpressCheckoutButton,
        response: KlarnaExpressCheckoutButtonAuthorizationResponse
    ) {
        eventSender.sendOnAuthorizedEvent(containerView, response)
    }

    override fun onError(
        view: KlarnaExpressCheckoutButton,
        error: KlarnaExpressCheckoutError
    ) {
        eventSender.sendKlarnaExpressCheckoutError(containerView, error)
    }
}
