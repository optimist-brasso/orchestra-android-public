package com.co.brasso.feature.shared.model.response.home

import com.google.gson.annotations.SerializedName

class HomeData {
    @SerializedName("banners")
    val banners: List<Banner>? = null

    @SerializedName("recommendations")
    val recommendations: List<Recommendation>? = null
}