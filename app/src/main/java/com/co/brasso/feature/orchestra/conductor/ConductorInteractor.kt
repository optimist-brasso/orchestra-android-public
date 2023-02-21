package com.co.brasso.feature.orchestra.conductor

import com.co.brasso.feature.shared.base.BaseInteractor
import com.co.brasso.feature.shared.model.favourite.FavouriteEntity
import com.co.brasso.feature.shared.repositories.FavouriteRepository
import com.co.brasso.feature.shared.repositories.OrchestraRepository
import io.reactivex.disposables.CompositeDisposable

class ConductorInteractor : BaseInteractor() {

    fun getOrchestra(
        compositeDisposable: CompositeDisposable?,
        token: String
    ) = OrchestraRepository.getOrchestra(compositeDisposable,token)

    fun addFavourite(
        compositeDisposable: CompositeDisposable?,
        favouriteEntity: FavouriteEntity?,
        token: String
    ) = FavouriteRepository.addFavourite(compositeDisposable, favouriteEntity, token)
}