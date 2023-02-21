package com.co.brasso.feature.shared.repositories

import com.co.brasso.feature.shared.base.BaseRepository
import com.co.brasso.feature.shared.model.emailLogin.EmailLoginEntity
import com.co.brasso.feature.shared.model.response.auth.Login
import com.co.brasso.utils.extension.getSubscription
import io.reactivex.Single
import io.reactivex.disposables.CompositeDisposable

object EmailLoginRepository : BaseRepository() {

    fun emailLogin(
        compositeDisposable: CompositeDisposable?,
        emailLoginEntity: EmailLoginEntity
    ): Single<Login> = Single.create { e ->
        apiService?.emailLogin(emailLoginEntity)
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