package com.co.brasso.feature.landing.myPage.tab.myPage.withdrawal

import com.co.brasso.feature.shared.base.BaseView
import com.co.brasso.feature.shared.model.response.resendotp.SuccessResponse

interface WithdrawalFragmentView : BaseView {
    fun withdrawSuccess(successResponse: SuccessResponse)
    fun showProgressBar()
    fun hideProgressBar()
}