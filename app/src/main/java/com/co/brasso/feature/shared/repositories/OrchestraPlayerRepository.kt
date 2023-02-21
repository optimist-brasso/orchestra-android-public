package com.co.brasso.feature.shared.repositories

import com.co.brasso.feature.shared.base.BaseRepository
import com.co.brasso.feature.shared.model.response.playerdetail.PlayerDetailResponse
import com.co.brasso.utils.extension.getSubscription
import io.reactivex.Single
import io.reactivex.disposables.CompositeDisposable

object OrchestraPlayerRepository : BaseRepository() {

    fun getPlayerList(
        compositeDisposable: CompositeDisposable?,
        token:String?,
        orchestraId: Int?,
        page:Int?
    ): Single<List<PlayerDetailResponse>> = Single.create { e ->
        apiService?.getOrchestraPlayerList(token,orchestraId,page)
            .getSubscription()
            ?.subscribe({ response ->
                if (response.isSuccessful && response.body()?.data != null) {
                    val players = response?.body()?.data
                    val pagination = response?.body()?.meta?.pagination
                    players?.forEach {
                        it.hasNext =
                            (pagination?.page ?: 0) < (pagination?.totalPages ?: 0)
                    }
                    e.onSuccess(players ?: listOf())
                } else {
                    e.onError(getError(response.code(), response.errorBody()?.string()))
                }
            }, {
                e.onError(getDefaultError(it))
            })?.let {
                compositeDisposable?.add(it)
            }
    }
}