package com.co.brasso.feature.shared.model.response.home

import com.google.gson.annotations.SerializedName
import java.io.Serializable

class Banner(
    @SerializedName("id")
    var id: String? = null,
    @SerializedName("title")
    var title: String? = null,
    @SerializedName("image")
    var image: String? = null,
    @SerializedName("link")
    var link: BannerLink? = null
) : Serializable