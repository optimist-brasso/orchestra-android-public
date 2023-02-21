package com.co.brasso.feature.session.sessionVideoPlayer

import com.co.brasso.feature.shared.base.BaseView
import com.co.brasso.feature.shared.model.response.cartList.CartListResponse

interface SessionVideoPlayerView : BaseView {
    fun success(message: String?)
    fun addToCartSuccess(cartListResponse: List<CartListResponse>?)
    fun purchaseSuccess(message: String?)
}