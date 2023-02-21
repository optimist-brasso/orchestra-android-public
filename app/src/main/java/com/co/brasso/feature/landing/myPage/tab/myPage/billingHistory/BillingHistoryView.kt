package com.co.brasso.feature.landing.myPage.tab.myPage.billingHistory

import com.co.brasso.feature.shared.base.BaseView
import com.co.brasso.feature.shared.model.response.billinghistory.NewBillingHistoryResponse

interface BillingHistoryView : BaseView {

    //    fun setBillingHistory(list: List<BillingHistoryResponse>)
    fun setBillingHistory(list: List<NewBillingHistoryResponse>)

    fun showProgressBar()
    fun hideProgressBar()
    fun showErrorView()
    fun hideErrorView()
}