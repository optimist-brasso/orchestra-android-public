package com.co.brasso.feature.shared.repositories

import com.co.brasso.feature.shared.base.BaseRepository
import com.co.brasso.feature.shared.model.response.sessionlayout.SessionLayoutResponse
import com.co.brasso.utils.extension.getSubscription
import io.reactivex.Single
import io.reactivex.disposables.CompositeDisposable

object SessionLayoutRepository : BaseRepository() {

    fun getSessionLayout(
        compositeDisposable: CompositeDisposable?, sessionId: Int,
        token:String?
    ): Single<SessionLayoutResponse> = Single.create { e ->
        apiService?.getSessionLayout(token,sessionId)
            .getSubscription()
            ?.subscribe({
                if (it.isSuccessful && it.body()?.data != null) {
                    e.onSuccess(it?.body()?.data ?: SessionLayoutResponse())
                } else {
                    e.onError(getError(it.code(), it.errorBody()?.string()))
                }
            }, {
                e.onError(getDefaultError(it))
            })?.let {
                compositeDisposable?.add(it)
            }
    }

}