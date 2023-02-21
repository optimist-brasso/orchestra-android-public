package com.co.brasso.feature.session.sessionDetail.sessionDetail

import com.co.brasso.feature.download.DownloadView
import com.co.brasso.feature.shared.model.response.cartList.CartListResponse
import com.co.brasso.feature.shared.model.response.sessionlayout.InstrumentDetailResponse

interface SessionDetailView : DownloadView {
    fun showProgressBar()
    fun hideProgressBar()
    fun success(instrumentDetailResponse: InstrumentDetailResponse)
    fun addToCartSuccess(cartListResponse: List<CartListResponse>?)
    fun purchaseSuccess(message: String?)
    fun noData()
    fun noInternet(errorNoInternetConnection: Int)
}