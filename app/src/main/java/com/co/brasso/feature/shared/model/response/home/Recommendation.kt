package com.co.brasso.feature.shared.model.response.home

import com.co.brasso.utils.constant.Constants
import com.google.gson.annotations.SerializedName

data class Recommendation(
    @SerializedName("id")
    var id: Int? = null,
    @SerializedName("title")
    var title: String? = null,
    @SerializedName("title_jp")
    var titleJp: String? = null,
    @SerializedName("introduction")
    var introduction: String? = null,
    @SerializedName("tags")
    var tags: MutableList<String>? = null,
    @SerializedName("record_time")
    var recordTime: String? = null,
    @SerializedName("release_date")
    var releaseDate: String? = null,
    @SerializedName("duration")
    var duration: Long? = null,
    @SerializedName("image")
    var image: String? = null,
    @SerializedName("organization")
    var organization: String? = null,
    @SerializedName("organization_diagram")
    var organizationDiagram: String? = null,
    @SerializedName("business_type")
    var businessType: String? = null,
    var viewType:String?=Constants.recommendedOrchestraSection
)