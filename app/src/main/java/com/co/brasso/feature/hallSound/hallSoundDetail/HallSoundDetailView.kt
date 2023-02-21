package com.co.brasso.feature.hallSound.hallSoundDetail

import com.co.brasso.feature.download.DownloadView
import com.co.brasso.feature.shared.model.response.cartList.CartListResponse
import com.co.brasso.feature.shared.model.response.hallsound.HallSoundResponse

interface HallSoundDetailView : DownloadView {
    fun updateViews()
    fun setHallSoundDetail(hallSound: HallSoundResponse)
    fun addToCartSuccess(cartListResponse: List<CartListResponse>?)
    fun showProgressBar()
    fun hideProgressBar()
    fun purchaseSuccess(message: String?,showCartCount:Boolean)
    fun noInternet(errorNoInternetConnection: Int)
}