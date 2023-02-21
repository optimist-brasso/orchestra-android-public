package com.co.brasso.feature.orchestra.conductorDetail

import com.co.brasso.feature.shared.model.favourite.FavouriteEntity
import com.co.brasso.feature.shared.repositories.FavouriteRepository
import com.co.brasso.feature.shared.repositories.OrchestraRepository
import io.reactivex.disposables.CompositeDisposable

class ConductorDetailInteractor {
    fun getOrchestraDetail(
        compositeDisposable: CompositeDisposable?,
        token: String,
        videoSupport:String?,
        id: Int?
    ) = OrchestraRepository.getOrchestraDetail(compositeDisposable, token, id,videoSupport)

    fun addFavourite(
        compositeDisposable: CompositeDisposable?,
        favouriteEntity: FavouriteEntity?,
        token: String
    ) = FavouriteRepository.addFavourite(compositeDisposable, favouriteEntity, token)
}