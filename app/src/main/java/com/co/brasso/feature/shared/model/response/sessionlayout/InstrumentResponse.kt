package com.co.brasso.feature.shared.model.response.sessionlayout

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class InstrumentResponse(
    val h: Double? = null,
    val w: Double? = null,
    val x: String? = null,
    val y: String? = null,
    val label: String? = null,
    val order: Int? = null,
    val created_at: String? = null,
    val identifier: String? = null,
    val description: String? = null,
    @SerializedName("musician_id")
    var musicianId: Int? = null,
    var instrument_id: Int? = null,
    var musician_image: String? = null,
    val instrument_title: String? = null,
    val recently_edited_at: String? = null,
    val instrument_musician: String? = null,
    val price: Int? = null,
    var sessionId: Int? = null,
    var isFromPremiumPage: Boolean? = false,
    var isClickedInstrument: Boolean? = false,
    var fromPage: String? = null
) : Serializable
