package com.co.brasso.feature.auth.signUp.email

import com.co.brasso.feature.shared.base.BaseView

interface EmailFragmentView  : BaseView {
    fun success(successMSG: String?)

    fun noData(errorMSG: String?)

    fun showError(errorMSG:Int)
}