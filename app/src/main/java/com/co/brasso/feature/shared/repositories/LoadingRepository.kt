package com.co.brasso.feature.shared.repositories

import com.android.billingclient.api.BillingClient
import com.android.billingclient.api.Purchase
import com.android.billingclient.api.QueryPurchasesParams
import com.co.brasso.feature.shared.base.BaseRepository
import com.co.brasso.feature.shared.model.request.FireBaseRegister
import com.co.brasso.feature.shared.model.response.appinfo.AppInfo
import com.co.brasso.utils.constant.ApiConstant
import com.co.brasso.utils.extension.getSubscription
import com.co.brasso.utils.extension.toJson
import com.co.brasso.utils.util.Logger
import com.google.firebase.messaging.FirebaseMessaging
import io.reactivex.Single
import io.reactivex.SingleEmitter
import io.reactivex.disposables.CompositeDisposable

object LoadingRepository : BaseRepository() {
    fun getAppInfo(
        compositeDisposable: CompositeDisposable?,
    ): Single<AppInfo> =
        Single.create { e ->
            apiService?.getAppInfo()
                .getSubscription()
                ?.subscribe({
                    if (it.isSuccessful && it.body()?.data != null) {
                        e.onSuccess(it.body()?.data!!)
                    } else {
                        e.onError(getError(it.code(), it.errorBody()?.string()))
                    }
                }, {
                    e.onError(getDefaultError(it))
                })?.let {
                    compositeDisposable?.add(it)
                }
        }

    fun queryPurchaseAsync(billingClient: BillingClient?): Single<MutableList<Purchase>> =
        Single.create { it ->
            val queryPurchasesParams =
                QueryPurchasesParams.newBuilder().setProductType(BillingClient.ProductType.INAPP)
                    .build()
            billingClient?.queryPurchasesAsync(
                queryPurchasesParams
            ) { billingResult, list ->
                if (billingResult.responseCode == BillingClient.BillingResponseCode.OK) {
                    it.onSuccess(list)
                } else {
                    it.onError(Throwable(ApiConstant.errorWhileFetchingPurchases))
                }
            }
        }

    fun registerFcmToken(
        token: String?,
        compositeDisposable: CompositeDisposable?
    ): Single<String> =
        Single.create { e ->
            getFcmToken(e) { fcmToken ->
                apiService?.registerFcm(
                    token,
                    getFcmTokenRequest(fcmToken).toJson()
                ).getSubscription()
                    ?.subscribe({
                        if (it.isSuccessful) {
                            e.onSuccess(fcmToken)
                        } else {
                            e.onError(getError(it.code(), it.errorBody()?.string()))
                        }
                    }, {
                        e.onError(it)
                    })?.let {
                        compositeDisposable?.add(it)
                    }
            }
        }

    fun unRegisterFcmToken(
        token: String?,
        compositeDisposable: CompositeDisposable?
    ): Single<String> =
        Single.create { e ->
            getFcmToken(e) { fcmToken ->
                apiService?.unRegisterFcm(
                    token
                ).getSubscription()
                    ?.subscribe({
                        if (it.isSuccessful) {
                            e.onSuccess(fcmToken)
                        } else {
                            e.onError(getError(it.code(), it.errorBody()?.string()))
                        }
                    }, {
                        e.onError(it)
                    })?.let {
                        compositeDisposable?.add(it)
                    }
            }
        }

    private fun getFcmTokenRequest(fcmToken: String?) =
        FireBaseRegister(
            fcmToken = fcmToken
        )


    private fun getFcmToken(
        e: SingleEmitter<String>, onFcmTokenReceived: (token: String) -> Unit
    ) {
        FirebaseMessaging.getInstance().token
            .addOnCompleteListener { task ->
                if (!task.isSuccessful || task.result.isNullOrBlank()) {
                    e.onError(Throwable(task.exception?.localizedMessage))
                } else {
                    Logger.d("FcmToken", task.result ?: "")
                    onFcmTokenReceived(task.result ?: "")
                }
            }
    }
}