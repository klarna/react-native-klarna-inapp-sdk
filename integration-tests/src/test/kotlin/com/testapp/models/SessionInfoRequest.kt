package com.testapp.models

internal data class SessionInfoRequest(
        val endpoint: String,
        val country: String
)