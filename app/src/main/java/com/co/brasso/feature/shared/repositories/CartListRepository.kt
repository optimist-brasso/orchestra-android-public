package com.co.brasso.feature.shared.repositories

import com.android.billingclient.api.BillingClient
import com.android.billingclient.api.ProductDetails
import com.android.billingclient.api.QueryProductDetailsParams
import com.co.brasso.feature.shared.base.BaseRepository
import com.co.brasso.feature.shared.model.request.cartItem.CartPurchase
import com.co.brasso.feature.shared.model.response.cartList.CartListResponse
import com.co.brasso.feature.shared.model.response.resendotp.SuccessResponse
import com.co.brasso.utils.constant.StringConstants
import com.co.brasso.utils.extension.getSubscription
import com.co.brasso.utils.extension.toJson
import io.reactivex.Single
import io.reactivex.disposables.CompositeDisposable

object CartListRepository : BaseRepository() {

    fun getCartList(
        compositeDisposable: CompositeDisposable?,
        token: String?
    ): Single<List<CartListResponse>> = Single.create { e ->
        apiService?.getCartList(token)
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

    fun deleteCartItem(
        compositeDisposable: CompositeDisposable?,
        token: String,
        id: Int?
    ): Single<SuccessResponse> = Single.create { e ->
        apiService?.deleteCartItem(token, id)
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

    fun purchaseCartItem(
        compositeDisposable: CompositeDisposable?,
        token: String,
        purchaseItemList: MutableList<CartListResponse>?
    ): Single<SuccessResponse> = Single.create { e ->
        apiService?.purchaseCartItem(token, CartPurchase(purchaseItemList).toJson())
            .getSubscription()
            ?.subscribe({
                if (it.isSuccessful && it.body()?.data != null) {
                    e.onSuccess(it.body()?.data?:SuccessResponse())
                } else {
                    e.onError(getError(it.code(), it.errorBody()?.string()))
                }
            }, {
                e.onError(getDefaultError(it))
            })?.let {
                compositeDisposable?.add(it)
            }
    }


    fun checkProductAvailability(productIdentifier:String?,billingClient: BillingClient?):Single<MutableList<ProductDetails>> = Single.create {e->
        val productList: ArrayList<QueryProductDetailsParams.Product> = arrayListOf()
        productList.add(
            QueryProductDetailsParams.Product.newBuilder()
                .setProductId(
                    productIdentifier ?: StringConstants.emptyString
                ).setProductType(BillingClient.ProductType.INAPP).build()
        )
        val queryProductDetailsParams =
            QueryProductDetailsParams.newBuilder()
                .setProductList(
                    productList
                ).build()

        billingClient?.queryProductDetailsAsync(queryProductDetailsParams) { billingResult, productDetailsList ->
            if (billingResult.responseCode == BillingClient.BillingResponseCode.OK && productDetailsList.isNotEmpty()) {
                e.onSuccess(productDetailsList)
            }else{
                e.onError(Throwable(StringConstants.productNotFound))
            }
        }

    }
}