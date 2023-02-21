package com.co.brasso.feature.shared.model.request.auth

import com.google.gson.annotations.SerializedName

data class ResetPassword(
    @SerializedName("token")
    var token: String? = null,
    @SerializedName("email")
    var email: String? = null,
    @SerializedName("password")
    var password: String? = null,
    @SerializedName("confirm_password")
    var confirmPassword: String? = null
)