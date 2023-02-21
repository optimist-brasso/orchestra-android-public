package com.co.brasso.feature.shared.model.response.billinghistory

import com.co.brasso.utils.constant.Constants
import com.google.gson.annotations.SerializedName

data class BillingHistoryResponse(

    @SerializedName("id")
    val id: Int? = null,

    @SerializedName("title")
    val title: String? = null,

    @SerializedName("title_jp")
    val titleJP: String? = null,

    @SerializedName("duration")
    val duration: String? = null,

    @SerializedName("date")
    val date: String? = null,

    @SerializedName("price") val price: Double? = null,

    @SerializedName("type")
    val type: String? = null,

    @SerializedName("session_type")
    val sessionType: String? = null,
    @SerializedName("musician_name")
    val musicianName:String?=null,

    @SerializedName("instrument")
    val instrument: String? = null,
    var titleDate: String? = null,
    val viewType: String = Constants.billHistory
)


