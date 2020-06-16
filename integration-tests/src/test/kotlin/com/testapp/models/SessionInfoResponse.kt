package com.testapp.models

internal data class SessionInfoResponse(
        val session: Session
)

internal data class Session(
        var client_token: String?
)