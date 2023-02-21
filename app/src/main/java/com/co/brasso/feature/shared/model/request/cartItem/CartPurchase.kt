package com.co.brasso.feature.shared.model.request.cartItem

import com.co.brasso.feature.shared.model.response.cartList.CartListResponse

data class CartPurchase(
    val items: MutableList<CartListResponse>? = null
)