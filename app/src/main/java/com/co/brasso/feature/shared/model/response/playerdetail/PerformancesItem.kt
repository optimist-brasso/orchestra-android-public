package com.co.brasso.feature.shared.model.response.playerdetail

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class PerformancesItem(
    val duration: String? = null,
    val image: String? = null,
    @SerializedName("title_jp")
    val titleJp: String? = null,
    val id: Int? = null,
    val title: String? = null,
    @SerializedName("record_time")
    val recordTime: String? = null,
    @SerializedName("release_date")
    val releaseDate: String? = null,
    @SerializedName("vr_file")
    val vrFile: String? = null
) : Serializable