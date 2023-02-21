package com.co.brasso.feature.orchestra.conductor

import com.co.brasso.feature.shared.base.BaseView
import com.co.brasso.feature.shared.model.response.GetBundleInfo
import com.co.brasso.feature.shared.model.response.hallsound.HallSoundResponse

interface ConductorView : BaseView {
    fun setOrchestraList(list: MutableList<HallSoundResponse>)

    fun showProgressBar()

    fun hideProgressBar()

    fun showErrorLayout()

    fun hideErrorLayout()

    fun setBundleList(bundleList: GetBundleInfo)}