package com.co.brasso.feature.session.sessionDetail.buyMultiPart

import com.co.brasso.feature.shared.base.BaseView
import com.co.brasso.feature.shared.model.response.cartList.CartListResponse
import com.co.brasso.feature.shared.model.response.multiPartList.MultiPartListResponseItem

interface BuyMultiPartView : BaseView {
    fun showProgressBar()
    fun hideProgressBar()
    fun success(multiPartListResponse: List<MultiPartListResponseItem>?)
    fun addToCartSuccess(list: List<CartListResponse>)
    fun purchaseSuccess(message: String?)
}