package com.co.brasso.feature.landing.myPage.tab.myPage.points.freePoints

import com.co.brasso.feature.shared.base.BaseView
import com.co.brasso.feature.shared.model.response.bundleList.freePoints.FreePointListResponse

interface FreePointsListView : BaseView {
    fun showProgressBar()
    fun hideProgressBar()
    fun setFreeBundleList(freePointListResponse: MutableList<FreePointListResponse>?)
}