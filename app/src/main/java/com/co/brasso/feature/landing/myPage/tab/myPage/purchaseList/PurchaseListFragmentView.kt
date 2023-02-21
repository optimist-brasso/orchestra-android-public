package com.co.brasso.feature.landing.myPage.tab.myPage.purchaseList

import com.co.brasso.feature.shared.base.BaseView
import com.co.brasso.feature.shared.model.response.purchaseList.OrchestraPurchaseListResponse

interface PurchaseListFragmentView : BaseView {
    fun showProgressBar()

    fun hideProgressBar()

    fun showErrorLayout()

    fun hideErrorLayout()

    fun setRecordingList(purchaseListResponse: OrchestraPurchaseListResponse)
}