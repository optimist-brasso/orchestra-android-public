package com.co.brasso.feature.auth.signUp.otp

import com.co.brasso.feature.shared.model.request.auth.EmailVerification
import com.co.brasso.feature.shared.model.request.auth.OtpVerification
import com.co.brasso.feature.shared.repositories.OtpFragmentRepository
import io.reactivex.disposables.CompositeDisposable

class OtpFragmentInteractor {
    fun verifyOtp(compositeDisposable: CompositeDisposable?, otpVerification: OtpVerification?) =
        OtpFragmentRepository.verifyOtp(compositeDisposable, otpVerification)

    fun resendToken(compositeDisposable: CompositeDisposable?, emailVerification: EmailVerification?) =
        OtpFragmentRepository.resendToken(compositeDisposable, emailVerification)
}