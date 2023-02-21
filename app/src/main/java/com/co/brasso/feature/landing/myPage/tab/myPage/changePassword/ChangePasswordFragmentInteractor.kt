package com.co.brasso.feature.landing.myPage.tab.myPage.changePassword

import com.co.brasso.feature.shared.model.request.auth.ChangePasswordVerification
import com.co.brasso.feature.shared.repositories.ChangePasswordFragmentRepository
import io.reactivex.disposables.CompositeDisposable

class ChangePasswordFragmentInteractor {
    fun changePassword(
        compositeDisposable: CompositeDisposable?,
        changePasswordVerification: ChangePasswordVerification?,
        token: String
    ) = ChangePasswordFragmentRepository.changePassword(compositeDisposable, changePasswordVerification,token)
}