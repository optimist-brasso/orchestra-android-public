package com.co.brasso.feature.shared.model.response.sessionFavourite

import com.google.gson.annotations.SerializedName


data class Instrument(

    @field:SerializedName("name")
    val name: String? = null,

    @field:SerializedName("id")
    val id: Int? = null,

    @field:SerializedName("is_favourite")
    var isFavourite: Boolean? = null
)