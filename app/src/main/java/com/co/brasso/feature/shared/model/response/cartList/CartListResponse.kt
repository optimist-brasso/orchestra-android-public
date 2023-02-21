package com.co.brasso.feature.shared.model.response.cartList

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class CartListResponse(

    val id: Int? = null,

    @SerializedName("orchestra_id")
    val orchestraId: Int? = null,

    @SerializedName("session_type")
    var sessionType: String? = null,

    @SerializedName("type")
    val type: String? = null,

    @SerializedName("is_premium")
    val isPremium: Boolean? = null,

    val title: String? = null,

    @SerializedName("title_jp")
    val titleJp: String? = null,

    val instrument: String? = null,

    val price: Double? = null,

    @SerializedName("musician_name")
    val musicianName: String? = null,

    @SerializedName("musician_id")
    val musicianId: Int? = null,

    @SerializedName("instrument_id")
    val instrumentId: Int? = null,

    @SerializedName("product_identifier")
    var identifier: String? = null,

    var isChecked: Boolean? = false,

    @SerializedName("release_date")
    val releaseDate: String? = null
) : Serializable