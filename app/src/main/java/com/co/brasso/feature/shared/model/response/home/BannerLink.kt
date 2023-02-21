package com.co.brasso.feature.shared.model.response.home

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class BannerLink(
    @SerializedName("type")
    val type:String?=null,
    @SerializedName("url")
    val url:String?=null,
    @SerializedName("description")
    val description:String?=null,
):Serializable