package com.co.brasso.feature.shared.model.favourite

import com.google.gson.annotations.SerializedName

data class FavouriteSessionEntity(
    @SerializedName("orchestra_id")
    var orchestraId: String? = null,
    @SerializedName("instrument_id")
    var instrumentId: String? = null,
    @SerializedName("musician_id")
    var musicianId: String? = null
)