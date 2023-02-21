package com.co.brasso.feature.shared.repositories

import com.co.brasso.feature.shared.base.BaseRepository
import com.co.brasso.feature.shared.model.response.hallsound.HallSoundResponse
import com.co.brasso.feature.shared.model.response.sessionFavourite.FavouriteSessionResponse
import com.co.brasso.utils.extension.getSubscription
import io.reactivex.Single
import io.reactivex.disposables.CompositeDisposable

object SearchRepository : BaseRepository() {

    fun getSearchData(
        compositeDisposable: CompositeDisposable?,
        token: String?,
        searchSlug: String?
    ): Single<MutableList<HallSoundResponse>> = Single.create { e ->
        apiService?.orchestraSearch(token, searchSlug)
            .getSubscription()
            ?.subscribe({
                if (it.isSuccessful && it.body()?.data != null) {
                    e.onSuccess((it.body()?.data ?: listOf()) as MutableList<HallSoundResponse>)
                } else {
                    e.onError(getError(it.code(), it.errorBody()?.string()))
                }
            }, {
                e.onError(getDefaultError(it))
            })?.let {
                compositeDisposable?.add(it)
            }
    }

    fun getSessionSearchData(
        compositeDisposable: CompositeDisposable?,
        token: String?,
        searchSlug: String?,
        page: Int?
    ): Single<MutableList<FavouriteSessionResponse>> = Single.create { e ->
        apiService?.sessionSearch(token, searchSlug, page)
            .getSubscription()
            ?.subscribe({response->
                if (response.isSuccessful && response.body()?.data != null) {
                    val sessionList = response?.body()?.data
                    val pagination = response?.body()?.meta?.pagination
                    sessionList?.forEach {
                        it.hasNext =
                            (pagination?.page ?: 0) < (pagination?.totalPages ?: 0)
                    }
                    e.onSuccess((sessionList ?: mutableListOf()) as MutableList<FavouriteSessionResponse>)
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