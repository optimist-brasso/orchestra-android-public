package com.co.brasso.feature.shared.repositories

import com.co.brasso.feature.shared.base.BaseRepository
import com.co.brasso.feature.shared.model.response.multiPartList.MultiPartListResponseItem
import com.co.brasso.utils.extension.getSubscription
import io.reactivex.Single
import io.reactivex.disposables.CompositeDisposable

object MultiPartListRepository : BaseRepository() {

    fun getMultiPartList(
        compositeDisposable: CompositeDisposable?,
        sessionID: Int?,
        token: String?
    ): Single<List<MultiPartListResponseItem>> = Single.create { e ->
        apiService?.getMultiPartList(token, sessionID)
            .getSubscription()
            ?.subscribe({
                if (it.isSuccessful && it.body()?.data != null) {
                    e.onSuccess(it.body()?.data?: listOf())
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