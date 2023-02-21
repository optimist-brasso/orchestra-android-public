package com.co.brasso.feature.auth.signUp.registration

import com.co.brasso.feature.shared.base.BaseView

interface RegistrationFragmentView : BaseView {

    fun success(successMSG: String?)

    fun navigateToMain()

    fun setLoginType(loginType: String)

    fun deleteProfilePicSuccess()

}