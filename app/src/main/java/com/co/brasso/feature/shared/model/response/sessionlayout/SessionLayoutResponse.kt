package com.co.brasso.feature.shared.model.response.sessionlayout

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class SessionLayoutResponse(
    val base_image: String? = null,
    var sessionId: Int? = null,
    @SerializedName("is_part_bought")
    var isPartBought: Boolean? = null,
    @SerializedName("is_premium_bought")
    var isPremiumBought: Boolean? = null,
    @SerializedName("layouts")
    var instrumentResponse: MutableList<InstrumentResponse>? = null) : Serializable

