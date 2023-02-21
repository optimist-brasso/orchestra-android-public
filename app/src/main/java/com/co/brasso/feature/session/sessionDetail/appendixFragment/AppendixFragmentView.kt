package com.co.brasso.feature.session.sessionDetail.appendixFragment

import com.co.brasso.feature.download.DownloadView
import com.co.brasso.feature.shared.model.response.cartList.CartListResponse
import com.co.brasso.feature.shared.model.response.sessionlayout.InstrumentDetailResponse

interface AppendixFragmentView : DownloadView {
    fun showProgressBar()
    fun hideProgressBar()
    fun getInstrumentSuccess(instrumentDetailResponse: InstrumentDetailResponse)
    fun addToCartSuccess(cartListResponse: List<CartListResponse>?)
    fun purchaseSuccess(message: String?)
    fun noInternet(errorNoInternetConnection: Int)
}