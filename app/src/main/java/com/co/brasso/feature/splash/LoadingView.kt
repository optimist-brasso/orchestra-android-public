package com.co.brasso.feature.splash

import com.co.brasso.feature.shared.base.BaseView
import com.co.brasso.feature.shared.model.response.myprofile.MyProfileResponse

interface LoadingView : BaseView {
    fun openStartingPage()
    fun closeApp()
    fun setProfileData(myProfileResponse: MyProfileResponse?)
    fun recallApi()

    fun showProgress()

    fun hideProgress()
}