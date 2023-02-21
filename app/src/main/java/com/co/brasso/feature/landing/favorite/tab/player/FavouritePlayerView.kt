package com.co.brasso.feature.landing.favorite.tab.player

import com.co.brasso.feature.shared.base.BaseView
import com.co.brasso.feature.shared.model.response.playerdetail.PlayerDetailResponse

interface FavouritePlayerView : BaseView {
    fun setMyPlayerFavourite(playerFavouriteList: List<PlayerDetailResponse>?)
    fun showProgressBar()
    fun hideProgressBar()
}