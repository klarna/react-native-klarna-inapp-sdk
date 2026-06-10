package com.klarna.mobile.sdk.reactnative.klarnanetwork.payment

import com.facebook.react.bridge.Arguments
import com.facebook.react.bridge.WritableMap
import com.klarna.mobile.sdk.klarna.network.payment.api.presentation.models.KlarnaPaymentPresentationContent
import com.klarna.mobile.sdk.klarna.network.payment.api.presentation.models.KlarnaPaymentPresentationIcon
import com.klarna.mobile.sdk.klarna.network.payment.api.presentation.models.KlarnaPaymentPresentationPaymentButton
import com.klarna.mobile.sdk.klarna.network.payment.api.presentation.models.KlarnaPaymentPresentationPaymentOption
import com.klarna.mobile.sdk.klarna.network.payment.api.presentation.models.KlarnaPaymentPresentationText
import com.klarna.mobile.sdk.klarna.network.payment.api.presentation.models.KlarnaPaymentPresentationTextPart

internal fun KlarnaPaymentPresentationContent.toWritableMap(): WritableMap = Arguments.createMap().apply {
    putString(PresentationKeys.INSTRUCTION, instruction.toString())
    putString(PresentationKeys.PAYMENT_STATUS, paymentStatus?.toString())
    val opt = paymentOption
    if (opt != null) putMap(PresentationKeys.PAYMENT_OPTION, opt.toWritableMap()) else putNull(PresentationKeys.PAYMENT_OPTION)
    val saved = savedPaymentOption
    if (saved != null) putMap(PresentationKeys.SAVED_PAYMENT_OPTION, saved.toWritableMap()) else putNull(PresentationKeys.SAVED_PAYMENT_OPTION)
}

internal fun KlarnaPaymentPresentationPaymentOption.toWritableMap(): WritableMap = Arguments.createMap().apply {
    putString(PresentationKeys.PAYMENT_OPTION_ID, paymentOptionId)
    val h = header; if (h != null) putMap(PresentationKeys.HEADER, h.toWritableMap()) else putNull(PresentationKeys.HEADER)
    val b = badge; if (b != null) putMap(PresentationKeys.BADGE, b.toWritableMap()) else putNull(PresentationKeys.BADGE)
    val sub = subheader; if (sub != null) putMap(PresentationKeys.SUBHEADER, sub.toWritableMap()) else putNull(PresentationKeys.SUBHEADER)
    val msg = message; if (msg != null) putMap(PresentationKeys.MESSAGE, msg.toWritableMap()) else putNull(PresentationKeys.MESSAGE)
    val t = terms; if (t != null) putMap(PresentationKeys.TERMS, t.toWritableMap()) else putNull(PresentationKeys.TERMS)
    val btn = paymentButton; if (btn != null) putMap(PresentationKeys.PAYMENT_BUTTON, btn.toWritableMap()) else putNull(PresentationKeys.PAYMENT_BUTTON)
    val ic = icon; if (ic != null) putMap(PresentationKeys.ICON, ic.toWritableMap()) else putNull(PresentationKeys.ICON)
}

internal fun KlarnaPaymentPresentationText.toWritableMap(): WritableMap = Arguments.createMap().apply {
    when (this@toWritableMap) {
        is KlarnaPaymentPresentationText.PlainText -> {
            putString(PresentationKeys.TYPE, PresentationKeys.TYPE_PLAIN_TEXT)
            putString(PresentationKeys.TEXT, text)
        }
        is KlarnaPaymentPresentationText.AttributedText -> {
            putString(PresentationKeys.TYPE, PresentationKeys.TYPE_ATTRIBUTED_TEXT)
            val parts = this@toWritableMap.parts
            if (parts != null) {
                val arr = Arguments.createArray()
                parts.forEach { arr.pushMap(it.toWritableMap()) }
                putArray(PresentationKeys.PARTS, arr)
            } else {
                putNull(PresentationKeys.PARTS)
            }
        }
    }
}

internal fun KlarnaPaymentPresentationTextPart.toWritableMap(): WritableMap = Arguments.createMap().apply {
    when (this@toWritableMap) {
        is KlarnaPaymentPresentationTextPart.PlainText -> {
            putString(PresentationKeys.TYPE, PresentationKeys.TYPE_PLAIN)
            putString(PresentationKeys.TEXT, text)
            val s = styles
            if (s != null) {
                val arr = Arguments.createArray(); s.forEach { arr.pushString(it.toString()) }
                putArray(PresentationKeys.STYLES, arr)
            } else putNull(PresentationKeys.STYLES)
        }
        is KlarnaPaymentPresentationTextPart.Link -> {
            putString(PresentationKeys.TYPE, PresentationKeys.TYPE_LINK)
            putString(PresentationKeys.TEXT, text)
            putString(PresentationKeys.URL, url)
            putString(PresentationKeys.CONTEXT, context?.toString())
            val s = styles
            if (s != null) {
                val arr = Arguments.createArray(); s.forEach { arr.pushString(it.toString()) }
                putArray(PresentationKeys.STYLES, arr)
            } else putNull(PresentationKeys.STYLES)
        }
    }
}

internal fun KlarnaPaymentPresentationPaymentButton.toWritableMap(): WritableMap = Arguments.createMap().apply {
    putString(PresentationKeys.TEXT, text)
    putString(PresentationKeys.IMAGE_URL, imageUrl)
    putString(PresentationKeys.IMAGE_ALIGNMENT, imageAlignment?.toString())
}

internal fun KlarnaPaymentPresentationIcon.toWritableMap(): WritableMap = Arguments.createMap().apply {
    putString(PresentationKeys.ALT, alt)
    putString(PresentationKeys.BADGE_IMAGE_URL, badgeImageUrl)
    putString(PresentationKeys.RECTANGLE_IMAGE_URL, rectangleImageUrl)
    putString(PresentationKeys.SQUARE_IMAGE_URL, squareImageUrl)
}

private object PresentationKeys {
    // KlarnaPaymentPresentationContent
    const val INSTRUCTION = "instruction"
    const val PAYMENT_STATUS = "paymentStatus"
    const val PAYMENT_OPTION = "paymentOption"
    const val SAVED_PAYMENT_OPTION = "savedPaymentOption"

    // KlarnaPaymentPresentationPaymentOption
    const val PAYMENT_OPTION_ID = "paymentOptionId"
    const val HEADER = "header"
    const val BADGE = "badge"
    const val SUBHEADER = "subheader"
    const val MESSAGE = "message"
    const val TERMS = "terms"
    const val PAYMENT_BUTTON = "paymentButton"
    const val ICON = "icon"

    // KlarnaPaymentPresentationText / KlarnaPaymentPresentationTextPart (shared keys)
    const val TYPE = "type"
    const val TEXT = "text"
    const val PARTS = "parts"
    const val STYLES = "styles"
    const val URL = "url"
    const val CONTEXT = "context"

    // Type discriminator values — KlarnaPaymentPresentationText
    const val TYPE_PLAIN_TEXT = "plainText"
    const val TYPE_ATTRIBUTED_TEXT = "attributedText"

    // Type discriminator values — KlarnaPaymentPresentationTextPart
    const val TYPE_PLAIN = "plain"
    const val TYPE_LINK = "link"

    // KlarnaPaymentPresentationPaymentButton
    const val IMAGE_URL = "imageUrl"
    const val IMAGE_ALIGNMENT = "imageAlignment"

    // KlarnaPaymentPresentationIcon
    const val ALT = "alt"
    const val BADGE_IMAGE_URL = "badgeImageUrl"
    const val RECTANGLE_IMAGE_URL = "rectangleImageUrl"
    const val SQUARE_IMAGE_URL = "squareImageUrl"
}
