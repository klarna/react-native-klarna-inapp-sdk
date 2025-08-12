package com.klarna.mobile.sdk.reactnative.signin.model

import com.klarna.mobile.sdk.api.signin.model.KlarnaSignInToken
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class KlarnaSignInTokenSerializable(
    @SerialName("access_token")
    val accessToken: String? = null,
    @SerialName("refresh_token")
    val refreshToken: String? = null,
    @SerialName("id_token")
    val idToken: String? = null,
    @SerialName("expires_in")
    val expiresIn: String? = null,
    @SerialName("scope")
    val scope: String? = null,
    @SerialName("token_type")
    val tokenType: String? = null,
) {
    companion object {
        fun from(klarnaSignInToken: KlarnaSignInToken) = KlarnaSignInTokenSerializable(
            accessToken = klarnaSignInToken.accessToken,
            refreshToken = klarnaSignInToken.refreshToken,
            idToken = klarnaSignInToken.idToken,
            expiresIn = klarnaSignInToken.expiresIn,
            scope = klarnaSignInToken.scope,
            tokenType = klarnaSignInToken.tokenType
        )
    }
}
