package com.co.brasso.feature.landing.myPage.tab.myPage.changePassword

import com.co.brasso.feature.shared.base.BaseView

interface ChangePasswordFragmentView : BaseView {
    fun success(successMSG: String?)

    fun noData(errorMSG: String?)

    fun showError(errorMSG: Int)
}