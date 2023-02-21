package com.co.brasso.feature.landing.myPage.tab.myPage

import com.co.brasso.feature.shared.repositories.LoadingRepository
import io.reactivex.disposables.CompositeDisposable

class MyPageMenuFragmentInteractor {

    fun unRegisterFcm(token:String?,compositeDisposable: CompositeDisposable?)=LoadingRepository.unRegisterFcmToken(token, compositeDisposable)
}