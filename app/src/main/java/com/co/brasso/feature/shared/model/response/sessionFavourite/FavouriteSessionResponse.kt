package com.co.brasso.feature.shared.model.response.sessionFavourite

import com.co.brasso.utils.constant.Constants
import com.google.gson.annotations.SerializedName

data class FavouriteSessionResponse(
    @field:SerializedName("orchestra")
    val orchestra: Orchestra? = null,

    @field:SerializedName("instrument")
    val instrument: Instrument? = null,

    @field:SerializedName("id")
    val id: Int? = null,

    @field:SerializedName("musician")
    val musician: Musician? = null,

    var hasNext: Boolean? = false,

    var viewType: String? = Constants.playerListView,

    @SerializedName("type")
    var type: String? = null
)
