package com.co.brasso.feature.landing.listSong.listSongOrchestra

import com.co.brasso.feature.shared.base.BaseView
import com.co.brasso.feature.shared.model.response.hallsound.HallSoundResponse

interface ListSongOrchestraView:BaseView {
    fun setSearchData(searchData: MutableList<HallSoundResponse>?)

    fun showProgressBar()

    fun hideProgressBar()
}