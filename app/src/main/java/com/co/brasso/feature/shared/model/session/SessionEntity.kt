package com.co.brasso.feature.shared.model.session

import com.google.gson.annotations.SerializedName

data class SessionEntity(

    @SerializedName("instrument_id")
    var instrumentId: Int? = null,
    @SerializedName("sessionId")
    var sessionId: Int? = null,
    @SerializedName("musician_id")
    var musicianId: Int? = null
)