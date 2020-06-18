package com.testapp.utils

import com.testapp.model.OrderLines
import com.testapp.model.Payload
import com.testapp.model.SessionRequest

internal object SessionHelper {

    fun getRequestSE(): SessionRequest {
        return createRequest("se", "sek", "en-SE")
    }

    fun getRequestNO(): SessionRequest {
        return createRequest("no", "nok", "en-US")
    }

    fun getRequestFI(): SessionRequest {
        return createRequest("fi", "eur", "en-US")
    }

    fun getRequestUS(): SessionRequest {
        return createRequest("us", "usd", "en-US")
    }

    fun getRequestUK(): SessionRequest {
        return createRequest("gb", "gbp", "en-GB")
    }

    fun getRequestAT(): SessionRequest {
        return createRequest("at", "eur", "en-AT")
    }

    fun getRequestDE(): SessionRequest {
        return createRequest("de", "eur", "en-DE")
    }

    private fun createRequest(country: String, currency: String, locale: String) = SessionRequest(
            "payments/v1/sessions",
            country,
            Payload(
                    country,
                    currency,
                    locale,
                    400,
                    listOf(OrderLines(
                            "Classic Low Bridge Sunglasses",
                            "sunglasses-$country",
                            1,
                            400,
                            400
                    ))
            )
    )
}