package com.co.brasso.feature.shared.model.response.bundleList.freePoints

import com.co.brasso.utils.constant.Constants
import com.google.gson.annotations.SerializedName

data class FreePointListResponse(

    @field:SerializedName("id")
    val id: Int? = null,

    @field:SerializedName("date")
    val date: String? = null,

    @field:SerializedName("point")
    val point: Int? = null,

    var hasNext: Boolean? = false,

    var viewType: String? = Constants.playerListView
)
