package com.co.brasso.feature.landing.listSong.listSongSession

import com.co.brasso.feature.shared.model.favourite.FavouriteSessionEntity
import com.co.brasso.feature.shared.repositories.FavouriteRepository
import com.co.brasso.feature.shared.repositories.SearchRepository
import io.reactivex.disposables.CompositeDisposable

class ListSongSessionInteractor {
    fun addFavourite(
        compositeDisposable: CompositeDisposable?,
        favouriteEntity: FavouriteSessionEntity?,
        token: String
    ) = FavouriteRepository.addSessionFavourite(compositeDisposable, favouriteEntity, token)

    fun getSessionSearch(
        token: String?,
        slug: String?,
        page: Int?,
        compositeDisposable: CompositeDisposable?
    ) = SearchRepository.getSessionSearchData(compositeDisposable, token, slug, page)
}