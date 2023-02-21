package com.co.brasso.feature.shared.model.favourite

import com.google.gson.annotations.SerializedName

data class FavouriteMusician(
    @SerializedName("musician_id")
    var musicianId: String? = null
)