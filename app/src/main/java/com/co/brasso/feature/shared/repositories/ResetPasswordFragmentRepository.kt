package com.co.brasso.feature.shared.repositories

import com.co.brasso.feature.shared.base.BaseRepository
import com.co.brasso.feature.shared.model.request.auth.ResetPassword
import com.co.brasso.feature.shared.model.response.resetpassword.ResetPasswordResponse
import com.co.brasso.utils.extension.getSubscription
import com.co.brasso.utils.extension.toJson
import io.reactivex.Single
import io.reactivex.disposables.CompositeDisposable

object ResetPasswordFragmentRepository : BaseRepository() {

    fun resetPassword(
        compositeDisposable: CompositeDisposable?,
        resetPassword: ResetPassword?
    ): Single<ResetPasswordResponse> = Single.create { e ->
        apiService?.resetPassword(resetPassword?.toJson())
            .getSubscription()
            ?.subscribe({
                if (it.isSuccessful) {
                    e.onSuccess(it.body()?.data ?: ResetPasswordResponse())
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