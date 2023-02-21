package com.co.brasso.feature.landing.favorite.tab.conductor

import com.co.brasso.feature.shared.base.BaseView
import com.co.brasso.feature.shared.model.response.favourite.FavouriteResponse

interface ConductorFavouriteView : BaseView {
    fun setSearchData(favouriteSearchList: List<FavouriteResponse>?)

    fun showProgressBar()

    fun hideProgressBar()
}