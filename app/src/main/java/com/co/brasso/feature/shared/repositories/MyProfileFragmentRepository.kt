package com.co.brasso.feature.shared.repositories

import com.co.brasso.feature.shared.base.BaseRepository
import com.co.brasso.feature.shared.model.response.myprofile.MyProfileResponse
import com.co.brasso.feature.shared.model.response.resendotp.SuccessResponse
import com.co.brasso.utils.extension.getSubscription
import io.reactivex.Single
import io.reactivex.disposables.CompositeDisposable

object MyProfileFragmentRepository : BaseRepository() {

    fun getProfile(
        compositeDisposable: CompositeDisposable?,
        token: String
    ): Single<MyProfileResponse> = Single.create { e ->
        apiService?.getProfile(token)
            .getSubscription()
            ?.subscribe({
                if (it.isSuccessful) {
                    e.onSuccess(it.body()?.data ?: MyProfileResponse())
                } else {
                    e.onError(getError(it.code(),it.errorBody()?.string()))
                }
            }, {
                e.onError(getDefaultError(it))
            })?.let {
                compositeDisposable?.add(it)
            }
    }

    fun setNotificationStatus(
        compositeDisposable: CompositeDisposable?,
        token: String
    ): Single<SuccessResponse> = Single.create { e ->
        apiService?.setNotificationStatus(token)
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