package com.co.brasso.feature.shared.repositories

import com.co.brasso.feature.shared.base.BaseRepository
import com.co.brasso.feature.shared.model.request.auth.SocialLoginRequest
import com.co.brasso.feature.shared.model.response.auth.Login
import com.co.brasso.utils.extension.getSubscription
import com.co.brasso.utils.extension.toJson
import io.reactivex.Single
import io.reactivex.disposables.CompositeDisposable

object AuthRepository : BaseRepository() {

    fun doSocialLogin(
        compositeDisposable: CompositeDisposable?,
        socialLoginRequest: SocialLoginRequest
    ): Single<Login> =
        Single.create { e ->
            apiService?.doSocialLogin(socialLoginRequest.toJson())
                .getSubscription()
                ?.subscribe({
                    if (it.isSuccessful) {
                        e.onSuccess(it.body()?.data ?: Login())
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