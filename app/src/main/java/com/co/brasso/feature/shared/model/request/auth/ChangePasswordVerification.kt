package com.co.brasso.feature.shared.model.request.auth

import com.google.gson.annotations.SerializedName

data class ChangePasswordVerification(
    @SerializedName("old_password")
    var oldPassword: String? = null,
    @SerializedName("new_password")
    var newPassword: String? = null,
    @SerializedName("confirm_password")
    var confirmPassword: String? = null
)