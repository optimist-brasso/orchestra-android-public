package com.co.brasso.feature.shared.model.response.registration

import com.google.gson.annotations.SerializedName

data class RegistrationResponse(

    @field:SerializedName("detail")
    val detail: String? = null,

    @field:SerializedName("message")
    val message: String? = null,

    @field:SerializedName("email")
    val email: String? = null
)