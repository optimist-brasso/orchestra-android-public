package com.co.brasso.feature.shared.model.response.sessionBoughtStatus

import com.co.brasso.feature.shared.model.response.multiPartList.MultiPartListResponseItem
import com.co.brasso.feature.shared.model.response.sessionlayout.SessionLayoutResponse

data class SessionBoughtStatus(
    val sessionLayoutResponse: SessionLayoutResponse? = null,
    val multipartResponse: List<MultiPartListResponseItem>? = null
)