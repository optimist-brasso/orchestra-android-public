package com.co.brasso.feature.landing.favorite.tab.session

import com.co.brasso.feature.shared.base.BaseView
import com.co.brasso.feature.shared.model.response.sessionFavourite.FavouriteSessionResponse

interface FavouriteSessionView : BaseView {
    fun setAdapter(favouriteSearchList: MutableList<FavouriteSessionResponse>?)

    fun showProgressBar()

    fun hideProgressBar()
}