package com.klarna.mobile.sdk.reactnative.klarnanetwork.payment

import com.facebook.react.bridge.Arguments
import com.facebook.react.bridge.WritableMap
import com.klarna.mobile.sdk.klarna.network.core.api.models.KlarnaAddress
import com.klarna.mobile.sdk.klarna.network.core.api.models.KlarnaCustomerProfile
import com.klarna.mobile.sdk.klarna.network.payment.api.KlarnaPaymentRequest
import com.klarna.mobile.sdk.klarna.network.payment.api.models.KlarnaCustomer
import com.klarna.mobile.sdk.klarna.network.payment.api.models.KlarnaPaymentRequestStateContext
import com.klarna.mobile.sdk.klarna.network.payment.api.models.KlarnaShipping
import com.klarna.mobile.sdk.klarna.network.payment.api.models.KlarnaShippingOption
import com.klarna.mobile.sdk.klarna.network.payment.api.models.KlarnaShippingRecipient

internal fun KlarnaPaymentRequest.toWritableMap(): WritableMap = Arguments.createMap().apply {
    putString(ResponseKeys.PAYMENT_REQUEST_ID, paymentRequestId)
    putString(ResponseKeys.STATE, state.toString())
    putString(ResponseKeys.PREVIOUS_STATE, previousState?.toString())
    putString(ResponseKeys.STATE_REASON, stateReason?.toString())
    putString(ResponseKeys.PAYMENT_REQUEST_REFERENCE, paymentRequestReference)
    val ctx = stateContext
    if (ctx != null) putMap(ResponseKeys.STATE_CONTEXT, ctx.toWritableMap()) else putNull(ResponseKeys.STATE_CONTEXT)
}

internal fun KlarnaPaymentRequestStateContext.toWritableMap(): WritableMap = Arguments.createMap().apply {
    putString(ResponseKeys.KLARNA_NETWORK_SESSION_TOKEN, klarnaNetworkSessionToken)
    val customer = klarnaCustomer
    if (customer != null) putMap(ResponseKeys.KLARNA_CUSTOMER, customer.toWritableMap()) else putNull(ResponseKeys.KLARNA_CUSTOMER)
    val ship = shipping
    if (ship != null) putMap(ResponseKeys.SHIPPING, ship.toWritableMap()) else putNull(ResponseKeys.SHIPPING)
}

internal fun KlarnaCustomer.toWritableMap(): WritableMap = Arguments.createMap().apply {
    putString(ResponseKeys.CUSTOMER_TOKEN, customerToken)
    putString(ResponseKeys.CUSTOMER_TOKEN_REFERENCE, customerTokenReference)
    val profile = customerProfile
    if (profile != null) putMap(ResponseKeys.CUSTOMER_PROFILE, profile.toWritableMap()) else putNull(ResponseKeys.CUSTOMER_PROFILE)
}

internal fun KlarnaCustomerProfile.toWritableMap(): WritableMap = Arguments.createMap().apply {
    val addr = address
    if (addr != null) putMap(ResponseKeys.ADDRESS, addr.toWritableMap()) else putNull(ResponseKeys.ADDRESS)
    putString(ResponseKeys.CUSTOMER_ID, customerId)
    putString(ResponseKeys.COUNTRY, country)
    putString(ResponseKeys.EMAIL, email)
    val ev = emailVerified
    if (ev != null) putBoolean(ResponseKeys.EMAIL_VERIFIED, ev) else putNull(ResponseKeys.EMAIL_VERIFIED)
    putString(ResponseKeys.FAMILY_NAME, familyName)
    putString(ResponseKeys.GIVEN_NAME, givenName)
    putString(ResponseKeys.LOCALE, locale)
    putString(ResponseKeys.PHONE, phone)
    val pv = phoneVerified
    if (pv != null) putBoolean(ResponseKeys.PHONE_VERIFIED, pv) else putNull(ResponseKeys.PHONE_VERIFIED)
}

internal fun KlarnaShipping.toWritableMap(): WritableMap = Arguments.createMap().apply {
    val addr = address
    if (addr != null) putMap(ResponseKeys.ADDRESS, addr.toWritableMap()) else putNull(ResponseKeys.ADDRESS)
    val rec = recipient
    if (rec != null) putMap(ResponseKeys.RECIPIENT, rec.toWritableMap()) else putNull(ResponseKeys.RECIPIENT)
    val opt = shippingOption
    if (opt != null) putMap(ResponseKeys.SHIPPING_OPTION, opt.toWritableMap()) else putNull(ResponseKeys.SHIPPING_OPTION)
    putString(ResponseKeys.SHIPPING_REFERENCE, shippingReference)
}

internal fun KlarnaAddress.toWritableMap(): WritableMap = Arguments.createMap().apply {
    putString(ResponseKeys.CITY, city)
    putString(ResponseKeys.COUNTRY, country)
    putString(ResponseKeys.POSTAL_CODE, postalCode)
    putString(ResponseKeys.REGION, region)
    putString(ResponseKeys.STREET_ADDRESS, streetAddress)
    putString(ResponseKeys.STREET_ADDRESS2, streetAddress2)
}

internal fun KlarnaShippingRecipient.toWritableMap(): WritableMap = Arguments.createMap().apply {
    putString(ResponseKeys.ATTENTION, attention)
    putString(ResponseKeys.EMAIL, email)
    putString(ResponseKeys.FAMILY_NAME, familyName)
    putString(ResponseKeys.GIVEN_NAME, givenName)
    putString(ResponseKeys.PHONE, phone)
}

internal fun KlarnaShippingOption.toWritableMap(): WritableMap = Arguments.createMap().apply {
    putString(ResponseKeys.SHIPPING_CARRIER, shippingCarrier)
    putString(ResponseKeys.SHIPPING_TYPE, shippingType.toString())
    val attrs = shippingTypeAttributes
    if (attrs != null) {
        val arr = Arguments.createArray()
        attrs.forEach { arr.pushString(it.toString()) }
        putArray(ResponseKeys.SHIPPING_TYPE_ATTRIBUTES, arr)
    } else {
        putNull(ResponseKeys.SHIPPING_TYPE_ATTRIBUTES)
    }
}

private object ResponseKeys {
    // KlarnaPaymentRequest
    const val PAYMENT_REQUEST_ID = "paymentRequestId"
    const val STATE = "state"
    const val PREVIOUS_STATE = "previousState"
    const val STATE_REASON = "stateReason"
    const val PAYMENT_REQUEST_REFERENCE = "paymentRequestReference"
    const val STATE_CONTEXT = "stateContext"

    // KlarnaPaymentRequestStateContext
    const val KLARNA_NETWORK_SESSION_TOKEN = "klarnaNetworkSessionToken"
    const val KLARNA_CUSTOMER = "klarnaCustomer"
    const val SHIPPING = "shipping"

    // KlarnaCustomer
    const val CUSTOMER_TOKEN = "customerToken"
    const val CUSTOMER_TOKEN_REFERENCE = "customerTokenReference"
    const val CUSTOMER_PROFILE = "customerProfile"

    // KlarnaCustomerProfile / KlarnaAddress / KlarnaShippingRecipient (shared keys)
    const val ADDRESS = "address"
    const val CUSTOMER_ID = "customerId"
    const val COUNTRY = "country"
    const val EMAIL = "email"
    const val EMAIL_VERIFIED = "emailVerified"
    const val FAMILY_NAME = "familyName"
    const val GIVEN_NAME = "givenName"
    const val LOCALE = "locale"
    const val PHONE = "phone"
    const val PHONE_VERIFIED = "phoneVerified"

    // KlarnaAddress
    const val CITY = "city"
    const val POSTAL_CODE = "postalCode"
    const val REGION = "region"
    const val STREET_ADDRESS = "streetAddress"
    const val STREET_ADDRESS2 = "streetAddress2"

    // KlarnaShipping
    const val RECIPIENT = "recipient"
    const val SHIPPING_OPTION = "shippingOption"
    const val SHIPPING_REFERENCE = "shippingReference"

    // KlarnaShippingRecipient
    const val ATTENTION = "attention"

    // KlarnaShippingOption
    const val SHIPPING_CARRIER = "shippingCarrier"
    const val SHIPPING_TYPE = "shippingType"
    const val SHIPPING_TYPE_ATTRIBUTES = "shippingTypeAttributes"
}
