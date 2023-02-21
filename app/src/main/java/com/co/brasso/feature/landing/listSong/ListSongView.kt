package com.co.brasso.feature.landing.listSong

import com.co.brasso.feature.shared.base.BaseView
import com.co.brasso.feature.shared.model.response.hallsound.HallSoundResponse

interface ListSongView:BaseView {

    fun setOrchestraList(list: List<HallSoundResponse>)

    fun showProgressBar()

    fun hideProgressBar()
}