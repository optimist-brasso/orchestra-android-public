package com.co.brasso.feature.shared.repositories

import android.util.Log
import com.android.billingclient.api.BillingClient
import com.android.billingclient.api.BillingResult
import com.android.billingclient.api.ConsumeParams
import com.android.billingclient.api.Purchase
import com.co.brasso.feature.shared.base.BaseRepository
import com.co.brasso.feature.shared.model.request.BoughtProducts
import com.co.brasso.feature.shared.model.response.resendotp.SuccessResponse
import com.co.brasso.utils.extension.getSubscription
import com.co.brasso.utils.extension.toJson
import com.co.brasso.utils.extension.toJsonString
import io.reactivex.Single
import io.reactivex.disposables.CompositeDisposable

object PaymentVerifyRepository : BaseRepository() {

    fun purchaseVerify(
        compositeDisposable: CompositeDisposable?,
        boughtProducts: BoughtProducts?,
        token: String
    ): Single<SuccessResponse> = Single.create { e ->
        Log.d("jpPurchase",boughtProducts.toJsonString())
        apiService?.paymentVerify(token, boughtProducts.toJson())
            .getSubscription()
            ?.subscribe({
                if (it.isSuccessful && it.body()?.data != null) {
                    e.onSuccess(it.body()!!.data!!)
                } else {
                    e.onError(getError(it.code(), it.errorBody()?.string()))
                }
            }, {
                e.onError(getDefaultError(it))
            })?.let {
                compositeDisposable?.add(it)
            }
    }

    fun enableConsumableProduct(
        purchase: Purchase?,
        billingClient: BillingClient?
    ): Single<Boolean> = Single.create { e ->
        val consumeParams =
            ConsumeParams.newBuilder()
                .setPurchaseToken(purchase?.purchaseToken ?: "")
                .build()
        billingClient?.consumeAsync(consumeParams) { result: BillingResult, error: String ->
            if (result.responseCode == 0)
                e.onSuccess(true)
            else
                e.onError(Throwable())
        }
    }
}