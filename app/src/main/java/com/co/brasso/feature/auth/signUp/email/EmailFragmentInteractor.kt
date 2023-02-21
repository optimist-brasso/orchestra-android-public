package com.co.brasso.feature.auth.signUp.email

import com.co.brasso.feature.shared.model.request.auth.EmailVerification
import com.co.brasso.feature.shared.repositories.EmailFragmentRepository
import io.reactivex.disposables.CompositeDisposable

class EmailFragmentInteractor {

    fun getOTP(compositeDisposable: CompositeDisposable?, emailVerification: EmailVerification?)
    = EmailFragmentRepository.getOTP(compositeDisposable,emailVerification)

}