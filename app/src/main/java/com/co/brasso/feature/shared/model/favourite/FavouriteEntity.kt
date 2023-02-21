package com.co.brasso.feature.shared.model.favourite

import com.google.gson.annotations.SerializedName

data class FavouriteEntity(
    @SerializedName("type")
    var type: String? = null,
    @SerializedName("orchestra_id")
    var orchestraID: String? = null
)