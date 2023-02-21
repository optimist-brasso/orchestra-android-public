package com.co.brasso.feature.shared.repositories

import com.co.brasso.feature.shared.base.BaseRepository
import com.co.brasso.feature.shared.model.response.home.HomeData
import com.co.brasso.utils.extension.getSubscription
import io.reactivex.Single
import io.reactivex.disposables.CompositeDisposable

object HomeRepository : BaseRepository() {
    fun getBanner(
        compositeDisposable: CompositeDisposable?,
        token : String
    ): Single<HomeData> =
        Single.create { e ->
            apiService?.getHomeData(token)
                .getSubscription()
                ?.subscribe({
                    if (it.isSuccessful && it.body()?.data!=null) {
                        e.onSuccess(it.body()?.data?: HomeData())
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