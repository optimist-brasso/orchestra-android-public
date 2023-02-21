package com.co.brasso.feature.landing.search.searchSongSession

import com.co.brasso.feature.shared.base.BaseView
import com.co.brasso.feature.shared.model.response.sessionFavourite.FavouriteSessionResponse

interface SearchSongSessionView : BaseView {
    fun setAdapter(sessionList: MutableList<FavouriteSessionResponse>?)

    fun showProgressBar()

    fun hideProgressBar()
}