package com.co.brasso.feature.auth.signUpOptions

import com.co.brasso.feature.shared.base.BaseView

interface SignUpOptionView : BaseView {
    fun navigateToMain()

    fun setLoginType(loginType: String)
}