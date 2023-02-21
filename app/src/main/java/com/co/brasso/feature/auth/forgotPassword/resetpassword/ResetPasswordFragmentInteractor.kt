package com.co.brasso.feature.auth.forgotPassword.resetpassword

import com.co.brasso.feature.shared.model.request.auth.ResetPassword
import com.co.brasso.feature.shared.repositories.ResetPasswordFragmentRepository
import io.reactivex.disposables.CompositeDisposable

class ResetPasswordFragmentInteractor {
    fun resetPassword(
        compositeDisposable: CompositeDisposable?,
        resetPassword: ResetPassword?
    ) = ResetPasswordFragmentRepository.resetPassword(compositeDisposable, resetPassword)
}