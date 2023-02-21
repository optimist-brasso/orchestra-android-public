package com.co.brasso.feature.landing.favorite

import com.co.brasso.feature.shared.repositories.FavouriteRepository
import io.reactivex.disposables.CompositeDisposable

class FavouriteInteractor {

    fun getFavouriteList(compositeDisposable: CompositeDisposable?,searchSlug: String?,token:String?)=FavouriteRepository.getFavourite(compositeDisposable,searchSlug,token)
}