package com.co.brasso.feature.auth.forgotPassword.emailverification

import com.co.brasso.feature.shared.base.BaseView

interface EmailVerificationFragmentView : BaseView {
    fun success(successMSG: String?)

    fun noData(errorMSG: String?)
}