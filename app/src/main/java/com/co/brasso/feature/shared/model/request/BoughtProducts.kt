package com.co.brasso.feature.shared.model.request

import com.co.brasso.feature.shared.model.response.cartList.CartListResponse
import com.google.gson.annotations.SerializedName

data class BoughtProducts(
    @SerializedName("google_receipt")
    var products: MutableList<BoughtInAppProduct>? = mutableListOf(),
    @SerializedName("items")
    var cartItems: MutableList<CartListResponse>? = mutableListOf()
)