package com.co.brasso.feature.landing.listSong.listSongOrchestra

import com.co.brasso.feature.shared.model.favourite.FavouriteEntity
import com.co.brasso.feature.shared.repositories.FavouriteRepository
import io.reactivex.disposables.CompositeDisposable

class ListSongOrchestraInteractor {
  fun addFavourite(
        compositeDisposable: CompositeDisposable?,
        favouriteEntity: FavouriteEntity?,
        token: String
    ) = FavouriteRepository.addFavourite(compositeDisposable, favouriteEntity, token)
}