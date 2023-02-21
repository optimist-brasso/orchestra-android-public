package com.co.brasso.feature.shared

import com.co.brasso.feature.shared.model.response.recordingList.RecordingListResponse

interface RecordingListClick {
    fun onClick(recordingListResponse: RecordingListResponse?)
}