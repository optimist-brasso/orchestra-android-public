package com.co.brasso.feature.shared.model.base

import com.co.brasso.feature.shared.model.response.meta.Meta
import com.google.gson.annotations.SerializedName


data class BaseArrayResponse<T>(
    @SerializedName("data")
    var data: List<T>? = null,
    @SerializedName("success")
    var success: Int?,
    @SerializedName("message")
    var message: String?,
    var meta: Meta? = Meta(),
)