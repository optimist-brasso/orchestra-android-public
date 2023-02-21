package com.co.brasso.feature.landing.myPage.tab.myPage.recordList

import com.co.brasso.feature.shared.base.BaseView
import com.co.brasso.feature.shared.model.response.recordingList.RecordingListResponse

interface RecordingFragmentView : BaseView {
    fun setRecordingList(recordingListResponse: List<RecordingListResponse>)

    fun showProgressBar()

    fun hideProgressBar()

    fun showErrorLayout()

    fun hideErrorLayout()
}