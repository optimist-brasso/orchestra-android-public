package com.co.brasso.feature.landing.myPage.tab.myPage.profile.editProfile

import com.co.brasso.feature.shared.base.BaseView
import com.co.brasso.feature.shared.model.response.myprofile.MyProfileResponse

interface EditMyProfileActivityView : BaseView {
    fun editProfileDetailSuccess()

    fun editProfilePicSuccess(myProfileResponse: MyProfileResponse)

    fun deleteProfilePicSuccess()
}
