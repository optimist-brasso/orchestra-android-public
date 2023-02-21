package com.co.brasso.feature.shared.repositories

import com.co.brasso.feature.shared.base.BaseRepository
import com.co.brasso.feature.shared.model.response.billinghistory.NewBillingHistoryResponse
import com.co.brasso.utils.extension.getSubscription
import io.reactivex.Single
import io.reactivex.disposables.CompositeDisposable

object BillingHistoryRepository : BaseRepository() {

//    fun getBillingHistory(
//        compositeDisposable: CompositeDisposable?,
//        token: String
//    ): Single<List<BillingHistoryResponse>> = Single.create { e ->
//        apiService?.getBillingHistory(token)
//            .getSubscription()
//            ?.subscribe({
//                if (it.isSuccessful && it.body()?.data != null) {
//                    e.onSuccess(it.body()?.data ?: listOf())
//                } else {
//                    e.onError(getError(it.code(), it.errorBody()?.string()))
//                }
//            }, {
//                e.onError(getDefaultError())
//            })?.let {
//                compositeDisposable?.add(it)
//            }
//    }

    fun getBillingHistory(
        compositeDisposable: CompositeDisposable?,
        token: String
    ): Single<List<NewBillingHistoryResponse>> = Single.create { e ->
        apiService?.getBillingHistory(token)
            .getSubscription()
            ?.subscribe({
                if (it.isSuccessful && it.body()?.data != null) {
                    e.onSuccess(it.body()?.data ?: listOf())
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