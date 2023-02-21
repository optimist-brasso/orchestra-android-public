package com.co.brasso.feature.auth.login

import com.co.brasso.feature.shared.base.BaseView

interface LoginView : BaseView {
    fun success(successMSG: Int)

    fun noData(errorMSG: String?)

    fun showEmailError(errorMSG:Int)

    fun showPasswordError(errorMSG:Int)

    fun navigateToMain()

    fun setLoginType(loginType: String)

}