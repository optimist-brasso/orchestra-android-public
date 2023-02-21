package com.co.brasso.feature.shared.repositories

import com.co.brasso.feature.shared.base.BaseRepository
import com.co.brasso.feature.shared.model.request.auth.EmailVerification
import com.co.brasso.feature.shared.model.response.emailverification.EmailFragmentResponse
import com.co.brasso.utils.extension.getSubscription
import com.co.brasso.utils.extension.toJson
import io.reactivex.Single
import io.reactivex.disposables.CompositeDisposable

object EmailVerificationFragmentRepository : BaseRepository() {

    fun forgotPassword(compositeDisposable: CompositeDisposable?, emailVerification: EmailVerification?): Single<EmailFragmentResponse> = Single.create { e ->
        apiService?.forgotPassword(emailVerification?.toJson())
            .getSubscription()
            ?.subscribe({
                if (it.isSuccessful) {
                    e.onSuccess(it.body()?.data?: EmailFragmentResponse())
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