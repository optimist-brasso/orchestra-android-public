package com.co.brasso.feature.landing.favorite

import com.co.brasso.feature.shared.base.BaseView
import com.co.brasso.feature.shared.model.response.favourite.FavouriteResponse

interface FavouriteView : BaseView {

    fun setFavouriteList(favouriteList: List<FavouriteResponse>?)

    fun showProgressBar()

    fun hideProgressBar()
}