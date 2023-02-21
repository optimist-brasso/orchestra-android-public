package com.co.brasso.feature.orchestra.conductorDetail

import com.co.brasso.feature.download.DownloadView
import com.co.brasso.feature.shared.model.response.hallsound.HallSoundResponse

interface ConductorDetailView : DownloadView {
    fun setConductorDetail(conductor: HallSoundResponse)
    fun showProgressBar()
    fun hideProgressBar()
    fun noInternet(errorNoInternetConnection: Int)
}