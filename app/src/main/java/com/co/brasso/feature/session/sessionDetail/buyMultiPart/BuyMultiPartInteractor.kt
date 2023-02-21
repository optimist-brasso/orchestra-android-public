package com.co.brasso.feature.session.sessionDetail.buyMultiPart

import com.android.billingclient.api.BillingClient
import com.co.brasso.feature.shared.base.BaseInteractor
import com.co.brasso.feature.shared.model.request.BoughtProducts
import com.co.brasso.feature.shared.model.request.cart.AddToCartList
import com.co.brasso.feature.shared.model.response.BuyProducts
import com.co.brasso.feature.shared.model.response.cartList.CartListResponse
import com.co.brasso.feature.shared.repositories.CartListRepository
import com.co.brasso.feature.shared.repositories.FavouriteRepository
import com.co.brasso.feature.shared.repositories.MultiPartListRepository
import com.co.brasso.feature.shared.repositories.PaymentVerifyRepository
import io.reactivex.Single
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.functions.BiFunction

class BuyMultiPartInteractor : BaseInteractor() {
    fun getMultiPartList(
        compositeDisposable: CompositeDisposable?,
        sessionID: Int?,
        token: String
    ) = MultiPartListRepository.getMultiPartList(compositeDisposable, sessionID, token)

    fun addToCart(
        compositeDisposable: CompositeDisposable?,
        addToCart: AddToCartList,
        token: String
    ) = FavouriteRepository.addToCart(compositeDisposable, addToCart, token)

    fun purchaseCartItem(
        compositeDisposable: CompositeDisposable?,
        token: String,
        purchaseItemList: MutableList<CartListResponse>?
    ) = CartListRepository.purchaseCartItem(compositeDisposable, token, purchaseItemList)

    fun purchaseCartItem(
        compositeDisposable: CompositeDisposable?,
        token: String,
        purchaseItemList: MutableList<CartListResponse>?,
        hallSoundIdentifier: String?,
        billingClient: BillingClient?
    ): Single<BuyProducts> = Single.zip(
        CartListRepository.purchaseCartItem(compositeDisposable, token, purchaseItemList),
        CartListRepository.checkProductAvailability(
            hallSoundIdentifier,
            billingClient
        ),
        BiFunction { success, products ->
            BuyProducts(success, products)
        }
    )

    fun purchaseVerify(
        compositeDisposable: CompositeDisposable?,
        boughtProducts: BoughtProducts?,
        token: String
    ) = PaymentVerifyRepository.purchaseVerify(compositeDisposable, boughtProducts, token)
}