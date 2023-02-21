package com.co.brasso.feature.download

import android.content.Context
import com.co.brasso.feature.shared.base.BaseView
import com.co.brasso.feature.shared.model.response.hallsound.HallSoundResponse

interface DownloadView : BaseView {
    fun getContext(): Context?

    fun downloadComplete()

    fun hidePlayButton()

    fun showPlayButton()

    fun hideDownloadButton()

    fun showStopDownloadButton()

    fun setProgress(progress:Int)

    fun showProgress()

    fun hideProgress()

    fun navigateToPlayer(orchestra : HallSoundResponse?,filePath:String?,fileName:String?)
}