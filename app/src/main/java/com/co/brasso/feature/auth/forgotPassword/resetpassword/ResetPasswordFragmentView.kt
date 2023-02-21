package com.co.brasso.feature.auth.forgotPassword.resetpassword

import com.co.brasso.feature.shared.base.BaseView

interface ResetPasswordFragmentView : BaseView {
    fun success(successMSG: String?)

    fun noData(errorMSG: String?)
}