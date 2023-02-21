package com.co.brasso.feature.landing.favorite.tab.session

import com.co.brasso.feature.shared.model.favourite.FavouriteSessionEntity
import com.co.brasso.feature.shared.repositories.FavouriteRepository
import io.reactivex.disposables.CompositeDisposable

class FavouriteSessionInteractor {

    fun getMySessionFavouriteList(
        compositeDisposable: CompositeDisposable?,
        token: String?,
        slug: String?, page: Int?
    ) = FavouriteRepository.getMySessionFavouriteList(compositeDisposable, token, slug, page)

    fun addFavourite(
        compositeDisposable: CompositeDisposable?,
        favouriteEntity: FavouriteSessionEntity?,
        token: String
    ) = FavouriteRepository.addSessionFavourite(compositeDisposable, favouriteEntity, token)
}