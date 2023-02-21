package com.co.brasso.feature.landing.myPage.tab.setting

import com.co.brasso.feature.shared.repositories.MyProfileFragmentRepository
import io.reactivex.disposables.CompositeDisposable

class MyPageSettingFragmentInteractor {

    fun setNotificationStatus(
        compositeDisposable: CompositeDisposable?,
        token: String
    ) = MyProfileFragmentRepository.setNotificationStatus(compositeDisposable, token)
}