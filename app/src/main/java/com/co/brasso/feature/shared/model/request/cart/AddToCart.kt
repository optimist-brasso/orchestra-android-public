package com.co.brasso.feature.shared.model.request.cart

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class AddToCart(
    @SerializedName("orchestra_id")
    var orchestraId: Int? = null,
    @SerializedName("type")
    var type: String? = null,
    @SerializedName("session_type")
    var sessionType: String? = null,
    @SerializedName("price")
    var price: Double? = null,
    @SerializedName("musician_id")
    var musicianId: Int? = null,
    @SerializedName("instrument_id")
    var instrumentId: Int? = null,
    @SerializedName("product_identifier")
    var identifier: String? = null,
    @SerializedName("release_date")
    val releaseDate: String? = null,
):Serializable