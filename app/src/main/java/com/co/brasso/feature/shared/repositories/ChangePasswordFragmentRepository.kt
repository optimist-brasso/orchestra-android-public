package com.co.brasso.feature.shared.repositories

import com.co.brasso.feature.shared.base.BaseRepository
import com.co.brasso.feature.shared.model.request.auth.ChangePasswordVerification
import com.co.brasso.feature.shared.model.response.changepassword.ChangePasswordFragmentResponse
import com.co.brasso.utils.extension.getSubscription
import com.co.brasso.utils.extension.toJson
import io.reactivex.Single
import io.reactivex.disposables.CompositeDisposable

object ChangePasswordFragmentRepository : BaseRepository() {

    fun changePassword(
        compositeDisposable: CompositeDisposable?,
        changePasswordVerification: ChangePasswordVerification?,
        token: String
    ): Single<ChangePasswordFragmentResponse> = Single.create { e ->
        apiService?.changePassword(token, changePasswordVerification?.toJson())
            .getSubscription()
            ?.subscribe({
                if (it.isSuccessful) {
                    e.onSuccess(it.body()?.data ?: ChangePasswordFragmentResponse())
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