package com.co.brasso.feature.shared.model.base

import com.co.brasso.feature.shared.model.response.meta.Meta
import com.google.gson.annotations.SerializedName


data class BaseResponse<T>(
    @SerializedName("data")
    var data: T? = null,
    @SerializedName("success")
    var success: Int? = null,
    @SerializedName("message")
    var message: String? = null,
    var meta: Meta? = Meta()
)