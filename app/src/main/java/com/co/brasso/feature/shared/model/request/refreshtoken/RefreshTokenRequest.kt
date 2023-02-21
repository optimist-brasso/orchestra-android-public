package com.co.brasso.feature.shared.model.request.refreshtoken

import com.co.brasso.BuildConfig
import com.co.brasso.utils.constant.ApiConstant
import com.google.gson.annotations.SerializedName


data class RefreshTokenRequest(
    @SerializedName("refresh_token")
    val refreshToken: String?,
    @SerializedName("client_id")
    val clientId: String = BuildConfig.CLIENT_ID,
    @SerializedName("client_secret")
    val clientSecret: String = BuildConfig.CLIENT_SECRET,
    @SerializedName("grant_type")
    val grantType: String = ApiConstant.grantTypeRefreshToken
)