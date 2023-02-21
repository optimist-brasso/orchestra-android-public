package com.co.brasso.feature.landing.listSong.listSongSession

import com.co.brasso.feature.shared.base.BaseView
import com.co.brasso.feature.shared.model.response.sessionFavourite.FavouriteSessionResponse

interface ListSongSessionView : BaseView {
    fun setAdapter(sessionList: MutableList<FavouriteSessionResponse>?)

    fun showProgressBar()

    fun hideProgressBar()
}