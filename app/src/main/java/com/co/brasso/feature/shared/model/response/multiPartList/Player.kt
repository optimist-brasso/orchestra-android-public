package com.co.brasso.feature.shared.model.response.multiPartList

import com.google.gson.annotations.SerializedName

data class Player(

    @field:SerializedName("image")
    val image: String? = null,

    @field:SerializedName("name")
    val name: String? = null,

    @field:SerializedName("id")
    val id: Int? = null
)