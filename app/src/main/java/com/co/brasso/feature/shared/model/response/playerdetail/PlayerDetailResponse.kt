package com.co.brasso.feature.shared.model.response.playerdetail

import com.co.brasso.utils.constant.Constants
import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class PlayerDetailResponse(

    val id: Int? = null,

    val name: String? = null,

    val instrument: Instrument? = null,

    val band: String? = null,

    val birthday: String? = null,

    @SerializedName("blood_group")
    val bloodGroup: String? = null,

    val birthplace: String? = null,

    val message: String? = null,

    @SerializedName("twitter_link")
    val twitter: String? = null,

    @SerializedName("instagram_link")
    val instagram: String? = null,

    @SerializedName("facebook_link")
    val facebook: String? = null,

    @SerializedName("youtube_link")
    val youtube: String? = null,

    @SerializedName("profile_link")
    val profileLink: String? = null,

    @SerializedName("is_favourite")
    var isFavourite: Boolean? = false,

    val performances: List<PerformancesItem>? = null,

    val images: List<String?>? = null,

    val image: String? = null,

    var hasNext: Boolean? = false,

    var manufacturer: String? = null,

    var viewType: String? = Constants.playerListView

) : Serializable
