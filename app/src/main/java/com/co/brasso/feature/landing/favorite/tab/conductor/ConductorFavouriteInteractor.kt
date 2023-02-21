package com.co.brasso.feature.landing.favorite.tab.conductor

import com.co.brasso.feature.shared.model.favourite.FavouriteEntity
import com.co.brasso.feature.shared.repositories.FavouriteRepository
import io.reactivex.disposables.CompositeDisposable

class ConductorFavouriteInteractor{

    fun addFavourite(
        compositeDisposable: CompositeDisposable?,
        favouriteEntity: FavouriteEntity?,
        token: String
    ) = FavouriteRepository.addFavourite(compositeDisposable, favouriteEntity, token)

}