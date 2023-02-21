package com.co.brasso.feature.auth.forgotPassword.emailverification

import com.co.brasso.feature.shared.model.request.auth.EmailVerification
import com.co.brasso.feature.shared.repositories.EmailVerificationFragmentRepository
import io.reactivex.disposables.CompositeDisposable

class EmailVerificationFragmentInteractor {
    fun forgotPassword(
        compositeDisposable: CompositeDisposable?,
        emailVerification: EmailVerification?
    ) = EmailVerificationFragmentRepository.forgotPassword(compositeDisposable, emailVerification)
}