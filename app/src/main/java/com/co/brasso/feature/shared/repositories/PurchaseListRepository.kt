package com.co.brasso.feature.shared.repositories

import com.co.brasso.feature.shared.base.BaseRepository
import com.co.brasso.feature.shared.model.response.purchaseList.OrchestraPurchaseListResponse
import com.co.brasso.utils.extension.getSubscription
import io.reactivex.Single
import io.reactivex.disposables.CompositeDisposable

object PurchaseListRepository : BaseRepository() {

    fun getPurchaseList(
        compositeDisposable: CompositeDisposable?,
        token: String?
    ): Single<OrchestraPurchaseListResponse> = Single.create { e ->
        apiService?.getPurchaseList(token)
            .getSubscription()
            ?.subscribe({
                if (it.isSuccessful && it.body()?.data != null) {
                    e.onSuccess(it.body()?.data ?: OrchestraPurchaseListResponse())
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