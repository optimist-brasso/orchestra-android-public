package com.co.brasso.feature.shared.model.response.favourite

import com.google.gson.annotations.SerializedName

data class FavouriteResponse(

    @field:SerializedName("duration")
    val duration: Long? = null,

    @field:SerializedName("image")
    val image: String? = null,

    @field:SerializedName("title_jp")
    val titleJp: String? = null,

    @field:SerializedName("id")
    val id: Int? = null,

    @field:SerializedName("title")
    val title: String? = null,

    @field:SerializedName("record_time")
    val recordTime: String? = null,

    @field:SerializedName("tags")
    val tags: List<String>? = null,

    @SerializedName("type")
    var type: String? = null,
    @SerializedName("venue_diagram")
    var venueDiagram: String? = null,
    @SerializedName("conductor_image")
    var conductorImage: String? = null,
    @SerializedName("session_image")
    var sessionImage: String? = null
)