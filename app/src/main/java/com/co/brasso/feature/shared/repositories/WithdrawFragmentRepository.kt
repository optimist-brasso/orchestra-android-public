package com.co.brasso.feature.shared.repositories

import com.co.brasso.feature.shared.base.BaseRepository
import com.co.brasso.feature.shared.model.response.resendotp.SuccessResponse
import com.co.brasso.feature.shared.model.withdraw.WithdrawEntity
import com.co.brasso.utils.extension.getSubscription
import com.co.brasso.utils.extension.toJson
import io.reactivex.Single
import io.reactivex.disposables.CompositeDisposable

object WithdrawFragmentRepository : BaseRepository() {

    fun proceedToWithdraw(
        compositeDisposable: CompositeDisposable?,
        token: String,
        withdrawEntity: WithdrawEntity
    ): Single<SuccessResponse> = Single.create { e ->
        apiService?.proceedToWithdraw(token,withdrawEntity.toJson())
            .getSubscription()
            ?.subscribe({
                if (it.isSuccessful) {
                    e.onSuccess(it.body()?.data ?: SuccessResponse())
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