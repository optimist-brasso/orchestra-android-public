package com.co.brasso.feature.shared.repositories

import com.co.brasso.feature.shared.base.BaseRepository
import com.co.brasso.feature.shared.model.request.auth.EmailVerification
import com.co.brasso.feature.shared.model.request.auth.OtpVerification
import com.co.brasso.feature.shared.model.response.otp.OtpFragmentResponse
import com.co.brasso.feature.shared.model.response.resendotp.SuccessResponse
import com.co.brasso.utils.extension.getSubscription
import com.co.brasso.utils.extension.toJson
import io.reactivex.Single
import io.reactivex.disposables.CompositeDisposable

object OtpFragmentRepository : BaseRepository() {

    fun verifyOtp(
        compositeDisposable: CompositeDisposable?,
        otpVerification: OtpVerification?
    ): Single<OtpFragmentResponse> = Single.create { e ->
        apiService?.verifyOtp(otpVerification.toJson())
            .getSubscription()
            ?.subscribe({
                if (it.isSuccessful) {
                    e.onSuccess(it.body()?.data ?: OtpFragmentResponse())
                } else {
                    e.onError(getError(it.code(),it.errorBody()?.string()))
                }
            }, {
                e.onError(getDefaultError(it))
            })?.let {
                compositeDisposable?.add(it)
            }
    }

    fun resendToken(
        compositeDisposable: CompositeDisposable?,
        emailVerification: EmailVerification?
    ): Single<SuccessResponse> = Single.create { e ->
        apiService?.resendToken(emailVerification?.toJson())
            .getSubscription()
            ?.subscribe({
                if (it.isSuccessful) {
                    e.onSuccess(it.body()?.data ?: SuccessResponse())
                } else {
                    e.onError(getError(it.code(),it.errorBody()?.string()))
                }
            }, {
                e.onError(getDefaultError(it))
            })?.let {
                compositeDisposable?.add(it)
            }
    }
}