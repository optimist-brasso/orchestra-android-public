package com.co.brasso.feature.shared.model.response.auth

import com.google.gson.annotations.SerializedName

data class Login(
    @SerializedName("token_type")
    val tokenType: String? = null,
    @SerializedName("expires_in")
    val expiresIn: Int? = null,
    @SerializedName("access_token")
    val accessToken: String? = null,
    @SerializedName("refresh_token")
    val refreshToken: String? = null
)