package com.co.brasso.feature.shared.repositories

import com.co.brasso.feature.shared.base.BaseRepository
import com.co.brasso.feature.shared.model.response.bundleList.BundleListResponse
import com.co.brasso.feature.shared.model.response.bundleList.freePoints.FreePointListResponse
import com.co.brasso.utils.extension.getSubscription
import io.reactivex.Single
import io.reactivex.disposables.CompositeDisposable

object BundleListRepository : BaseRepository() {
    fun getBundleList(
        compositeDisposable: CompositeDisposable?,
        token: String
    ): Single<BundleListResponse> =
        Single.create { e ->
            apiService?.getBundleList(token)
                .getSubscription()
                ?.subscribe({
                    if (it.isSuccessful && it.body()?.data != null) {
                        e.onSuccess(it.body()?.data ?: BundleListResponse())
                    } else {
                        e.onError(HomeRepository.getError(it.code(), it.errorBody()?.string()))
                    }
                }, {
                    e.onError(HomeRepository.getDefaultError(it))
                })?.let {
                    compositeDisposable?.add(it)
                }
        }

    fun getFreePointsList(
        compositeDisposable: CompositeDisposable?,
        token: String,
        page: Int?
    ): Single<List<FreePointListResponse>> =
        Single.create { e ->
            apiService?.getFreeBundleList(token, page)
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
                    e.onError(HomeRepository.getDefaultError(it))
                })?.let {
                    compositeDisposable?.add(it)
                }
        }
}