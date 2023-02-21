package com.co.brasso.feature.player.playerDetail

import com.co.brasso.feature.shared.model.favourite.FavouriteMusician
import com.co.brasso.feature.shared.repositories.FavouriteRepository
import com.co.brasso.feature.shared.repositories.PlayerDetailRepository
import io.reactivex.disposables.CompositeDisposable

class PlayerDetailFragmentInteractor {
    fun getPlayerDetail(compositeDisposable: CompositeDisposable?, id: Int?, token: String?) =
        PlayerDetailRepository.getPlayerDetail(compositeDisposable, id, token)

    fun favouriteMusician(
        compositeDisposable: CompositeDisposable?,
        favouriteMusician: FavouriteMusician?,
        token: String?
    ) = FavouriteRepository.addMusicianFavourite(compositeDisposable, favouriteMusician, token)
}