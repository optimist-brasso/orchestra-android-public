package com.co.brasso.feature.session

import com.co.brasso.feature.shared.base.BaseView
import com.co.brasso.feature.shared.model.response.sessionBoughtStatus.SessionBoughtStatus
import com.co.brasso.feature.shared.model.response.sessionlayout.SessionLayoutResponse

interface SessionLayoutView : BaseView {

    fun setSessionLayout(sessionLayoutResponse: SessionBoughtStatus)

    fun showDialogBox()

    fun popUpBackStack()

    fun navigateToDetail()
    fun navigateToPlayerDetail(musicianId: Int?)
}