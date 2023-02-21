package com.co.brasso.feature.shared.repositories

import com.co.brasso.feature.shared.base.BaseRepository
import com.co.brasso.feature.shared.model.response.playerdetail.PlayerDetailResponse
import com.co.brasso.utils.extension.getSubscription
import io.reactivex.Single
import io.reactivex.disposables.CompositeDisposable

object PlayerDetailRepository : BaseRepository() {

    fun getPlayerDetail(
        compositeDisposable: CompositeDisposable?,
        id: Int?,
        token: String?
    ): Single<PlayerDetailResponse> = Single.create { e ->
        apiService?.getPlayerDetail(token, id)
            .getSubscription()
            ?.subscribe({
                if (it.isSuccessful && it.body()?.data != null) {
                    e.onSuccess(it.body()?.data ?: PlayerDetailResponse())
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