package com.co.brasso.feature.shared.model.request.auth

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class SocialLoginRequest(
    @SerializedName("access_token")
    @Expose
    private val access_token: String? = null,

    @SerializedName("type")
    @Expose
    private val type: String? = null,

    @SerializedName("grant_type")
    @Expose
    private val grantType: String? = null,

    @SerializedName("user_identifier")
    @Expose
    private val accessTokenSecret: String? = null,
)