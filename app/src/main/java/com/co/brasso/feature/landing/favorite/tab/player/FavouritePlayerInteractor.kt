package com.co.brasso.feature.landing.favorite.tab.player

import com.co.brasso.feature.shared.model.favourite.FavouriteMusician
import com.co.brasso.feature.shared.repositories.FavouriteRepository
import io.reactivex.disposables.CompositeDisposable

class FavouritePlayerInteractor {

    fun getMyPlayerFavouriteList(
        compositeDisposable: CompositeDisposable?,
        token: String?,
        slug: String?,
        page: Int?
    ) = FavouriteRepository.getMyPlayerFavouriteList(compositeDisposable, token, slug, page)

    fun favouriteMusician(
        compositeDisposable: CompositeDisposable?,
        favouriteMusician: FavouriteMusician?,
        token: String?
    ) = FavouriteRepository.addMusicianFavourite(compositeDisposable, favouriteMusician, token)
}