package com.testapp.models

internal data class SessionRequest(
        val endpoint: String,
        val country: String,
        val payload: Payload
)

internal data class Payload(
        val purchase_country: String,
        val purchase_currency: String,
        val locale: String,
        val order_amount: Int,
        val order_lines: List<OrderLines>
)

internal data class OrderLines (
        val name: String,
        val merchant_data: String,
        val quantity: Int,
        val total_amount: Int,
        val unit_price: Int
)