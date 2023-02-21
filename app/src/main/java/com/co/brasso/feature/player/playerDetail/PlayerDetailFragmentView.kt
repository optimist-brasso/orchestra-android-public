package com.co.brasso.feature.player.playerDetail

import com.co.brasso.feature.shared.base.BaseView
import com.co.brasso.feature.shared.model.response.playerdetail.PlayerDetailResponse

interface PlayerDetailFragmentView : BaseView {
    fun success(playerDetailResponse: PlayerDetailResponse)
    fun showProgressBar()
    fun hideProgressBar()
    fun noInternet(errorNoInternetConnection: Int)
}