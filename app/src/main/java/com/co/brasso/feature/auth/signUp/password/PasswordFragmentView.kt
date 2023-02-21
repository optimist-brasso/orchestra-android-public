package com.co.brasso.feature.auth.signUp.password

import com.co.brasso.feature.shared.base.BaseView

interface PasswordFragmentView : BaseView {
    fun success(password: String?)

    fun showError(errorMSG: Int)
}