package com.co.brasso.feature.shared.model.response.multiPartList

import com.google.gson.annotations.SerializedName

data class MultiPartListResponseItem(

    @field:SerializedName("premium_video_price")
    val premiumVideoPrice: Int? = null,

    @field:SerializedName("is_part_bought")
    var isPartBought: Boolean? = null,

    @field:SerializedName("is_premium_bought")
    var isPremiumBought: Boolean? = null,

    @field:SerializedName("combo_price")
    val comboPrice: Double? = null,

    @field:SerializedName("name")
    val name: String? = null,

    @field:SerializedName("part_price")
    val partPrice: Double? = null,

    @field:SerializedName("id")
    val id: Int? = null,

    @SerializedName("part_identifier")
    val partIdentifier: String? = null,
    @SerializedName("musician_image")
    val musicianImage: String? = null,
    @field:SerializedName("player")
    val player: Player? = null,

    var isChecked: Boolean? = false,

    var fromPage: String? = null
)