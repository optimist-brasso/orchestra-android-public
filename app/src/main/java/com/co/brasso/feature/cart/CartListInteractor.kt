package com.co.brasso.feature.cart

import com.co.brasso.database.room.AppDatabase
import com.co.brasso.feature.shared.model.request.BoughtProducts
import com.co.brasso.feature.shared.model.response.cartList.CartListResponse
import com.co.brasso.feature.shared.repositories.CartListRepository
import com.co.brasso.feature.shared.repositories.NotificationRepository
import com.co.brasso.feature.shared.repositories.PaymentVerifyRepository
import io.reactivex.disposables.CompositeDisposable

class CartListInteractor {
    fun getCartList(compositeDisposable: CompositeDisposable?, token: String?) =
        CartListRepository.getCartList(compositeDisposable, token)

    fun deleteCartItem(compositeDisposable: CompositeDisposable?, token: String, id: Int?) =
        CartListRepository.deleteCartItem(compositeDisposable, token, id)

    fun purchaseCartItem(
        compositeDisposable: CompositeDisposable?,
        token: String,
        purchaseItemList: MutableList<CartListResponse>?
    ) = CartListRepository.purchaseCartItem(compositeDisposable, token, purchaseItemList)

    fun getAllNotifications(compositeDisposable: CompositeDisposable?, appDatabase: AppDatabase?) =
        NotificationRepository.getAllNotificationsFromDb(compositeDisposable, appDatabase)

    fun purchaseVerify(
        compositeDisposable: CompositeDisposable?,
        boughtProducts: BoughtProducts?,
        token: String
    ) = PaymentVerifyRepository.purchaseVerify(compositeDisposable, boughtProducts, token)
}