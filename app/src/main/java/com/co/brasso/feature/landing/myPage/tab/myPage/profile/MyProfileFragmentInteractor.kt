package com.co.brasso.feature.landing.myPage.tab.myPage.profile

import com.co.brasso.feature.shared.repositories.MyProfileFragmentRepository
import io.reactivex.disposables.CompositeDisposable

class MyProfileFragmentInteractor {
    fun getProfile(
        compositeDisposable: CompositeDisposable?,
        token: String
    ) = MyProfileFragmentRepository.getProfile(compositeDisposable, token)
}