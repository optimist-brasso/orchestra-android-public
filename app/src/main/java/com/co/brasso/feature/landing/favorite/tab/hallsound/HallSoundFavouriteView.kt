package com.co.brasso.feature.landing.favorite.tab.hallsound

import com.co.brasso.feature.shared.base.BaseView
import com.co.brasso.feature.shared.model.response.favourite.FavouriteResponse

interface HallSoundFavouriteView : BaseView {
    fun setSearchData(favouriteSearchList: List<FavouriteResponse>?)

    fun showProgressBar()

    fun hideProgressBar()
}