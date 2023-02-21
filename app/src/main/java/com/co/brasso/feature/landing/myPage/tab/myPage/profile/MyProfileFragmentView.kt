package com.co.brasso.feature.landing.myPage.tab.myPage.profile

import com.co.brasso.feature.shared.base.BaseView
import com.co.brasso.feature.shared.model.response.myprofile.MyProfileResponse

interface MyProfileFragmentView : BaseView {
    fun success(myProfileResponse: MyProfileResponse?)

    fun hideProgressbar()

    fun showProgressbar()

   fun hideMainView()
}