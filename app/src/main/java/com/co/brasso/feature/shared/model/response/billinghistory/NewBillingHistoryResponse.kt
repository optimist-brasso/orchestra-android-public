package com.co.brasso.feature.shared.model.response.billinghistory

import com.google.gson.annotations.SerializedName

data class NewBillingHistoryResponse(

    @SerializedName("id")
    val id: Int? = null,

    @SerializedName("paid_point")
    val paidPoint: String? = null,

    @SerializedName("free_point")
    val freePoint: String? = null,

    @SerializedName("price")
    val price: Double? = null,

    @SerializedName("purchase_date")
    val purchaseDate: String? = null
)


