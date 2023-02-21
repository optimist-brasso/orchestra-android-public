package com.co.brasso.feature.shared.model.response

import com.google.gson.annotations.SerializedName

data class ErrorResponse(
    @SerializedName("error")
    var error: Error? = null,

    @SerializedName("errors")
    var errorList: List<Error?>? = null
)