package com.co.brasso.feature.hallSound.hallSoundDetail

import com.co.brasso.feature.shared.base.BaseInteractor
import com.co.brasso.feature.shared.model.favourite.FavouriteEntity
import com.co.brasso.feature.shared.model.request.BoughtProducts
import com.co.brasso.feature.shared.model.request.cart.AddToCartList
import com.co.brasso.feature.shared.repositories.FavouriteRepository
import com.co.brasso.feature.shared.repositories.OrchestraRepository
import com.co.brasso.feature.shared.repositories.PaymentVerifyRepository
import io.reactivex.disposables.CompositeDisposable

class HallSoundDetailInteractor : BaseInteractor() {
    fun getHallSoundDetail(
        compositeDisposable: CompositeDisposable?,
        token: String,
        id: Int?,
        videoSupport:String?
    ) = OrchestraRepository.getOrchestraDetail(compositeDisposable, token, id,videoSupport)

    fun addFavourite(
        compositeDisposable: CompositeDisposable?,
        favouriteEntity: FavouriteEntity?,
        token: String
    ) = FavouriteRepository.addFavourite(compositeDisposable, favouriteEntity, token)

    fun addToCart(
        compositeDisposable: CompositeDisposable?,
        addToCart: AddToCartList,
        token: String
    ) = FavouriteRepository.addToCart(compositeDisposable, addToCart, token)

    fun purchaseVerify(
        compositeDisposable: CompositeDisposable?,
        boughtProducts: BoughtProducts?,
        token: String
    ) = PaymentVerifyRepository.purchaseVerify(compositeDisposable, boughtProducts, token)
}