package com.co.brasso.feature.cart

import com.co.brasso.feature.shared.base.BaseView
import com.co.brasso.feature.shared.model.response.cartList.CartListResponse

interface CartListView : BaseView {
    fun showProgressBar()
    fun hideProgressBar()
    fun setCartList(cartListResponse: List<CartListResponse>)
    fun deleteSuccess(message: String?, id: Int?)
    fun purchaseSuccess(message: String?)
}