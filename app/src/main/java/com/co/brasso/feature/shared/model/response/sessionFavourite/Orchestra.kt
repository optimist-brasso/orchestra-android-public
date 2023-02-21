package com.co.brasso.feature.shared.model.response.sessionFavourite

import com.google.gson.annotations.SerializedName

data class Orchestra(

    @field:SerializedName("title")
    val title: String? = null,

    @field:SerializedName("title_jp")
    val titleJp: String? = null,

    @field:SerializedName("id")
    val id: Int? = null
)