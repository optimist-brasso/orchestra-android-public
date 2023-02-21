package com.co.brasso.feature.shared.model.emailLogin

import com.google.gson.annotations.SerializedName

data class EmailLoginEntity(

    @SerializedName("email")
    var email: String? = null,

    @SerializedName("password")
    var password: String? = null,

    @SerializedName("grant_type")
    var grantType: String? = null,

    @SerializedName("refresh_token")
    var refreshToken: String? = null

)