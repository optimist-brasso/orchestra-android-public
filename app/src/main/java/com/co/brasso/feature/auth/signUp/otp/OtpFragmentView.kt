package com.co.brasso.feature.auth.signUp.otp

import com.co.brasso.feature.shared.base.BaseView

interface OtpFragmentView  : BaseView {
    fun success(successMSG: String?)

    fun resendSuccess(successMSG: String?)

    fun noData(errorMSG: String?)

    fun showError(errorMSG:Int)
}