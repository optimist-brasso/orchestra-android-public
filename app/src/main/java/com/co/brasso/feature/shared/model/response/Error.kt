package com.co.brasso.feature.shared.model.response

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class Error (
    @SerializedName("title")
    @Expose
     val title: String? = null,
    @SerializedName("detail")
    @Expose
     val detail: String? = null

)