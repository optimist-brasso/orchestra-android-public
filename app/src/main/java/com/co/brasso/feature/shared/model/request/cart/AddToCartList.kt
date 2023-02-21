package com.co.brasso.feature.shared.model.request.cart

import com.google.gson.annotations.SerializedName

data class AddToCartList(
    @SerializedName("items")
    var orchestraItems: List<AddToCart>? = null,
)