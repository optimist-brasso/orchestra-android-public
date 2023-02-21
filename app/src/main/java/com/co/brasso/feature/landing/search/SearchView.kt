package com.co.brasso.feature.landing.search

import com.co.brasso.feature.shared.base.BaseView
import com.co.brasso.feature.shared.model.response.hallsound.HallSoundResponse

interface SearchView:BaseView {
    fun setOrchestraSearchData(searchList: MutableList<HallSoundResponse>?)

    fun showProgressBar()

    fun hideProgressBar()
}