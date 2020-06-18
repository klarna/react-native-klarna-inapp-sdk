package com.testapp.model

internal data class SessionInfoResponse(
        val session: Session
)

internal data class Session(
        var client_token: String?,
        var payment_method_categories: List<PaymentMethodCategory>
)

internal data class PaymentMethodCategory(
        var identifier: String
)