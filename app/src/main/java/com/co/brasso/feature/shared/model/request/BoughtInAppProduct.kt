package com.co.brasso.feature.shared.model.request

import com.google.gson.annotations.SerializedName


data class BoughtInAppProduct(
    @SerializedName("orderId")
    val orderId: String? = null,

    @SerializedName("packageName")
    val packageName: String? = null,

    @SerializedName("productId")
    val productId: String? = null,

    @SerializedName("purchaseTime")
    val purchaseTime: Long? = null,

    @SerializedName("purchaseState")
    val purchaseState: Int? = null,

    @SerializedName("purchaseToken")
    val purchaseToken: String? = null,

    @SerializedName("quantity")
    val quantity: Int? = null,

    @SerializedName("acknowledged")
    val acknowledged: Boolean? = null,

    @SerializedName("consumptionState")
    val consumptionState:Int?=1
)
