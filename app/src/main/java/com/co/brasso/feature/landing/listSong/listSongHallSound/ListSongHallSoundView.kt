package com.co.brasso.feature.landing.listSong.listSongHallSound

import com.co.brasso.feature.shared.base.BaseView
import com.co.brasso.feature.shared.model.response.hallsound.HallSoundResponse

interface ListSongHallSoundView:BaseView {
    fun setSearchData(searchData: MutableList<HallSoundResponse>?)

    fun showProgressBar()

    fun hideProgressBar()
}