package com.co.brasso.feature.landing.search.searchOrchestra

import com.co.brasso.feature.shared.base.BasePresenter
import com.co.brasso.feature.shared.model.favourite.FavouriteEntity

class SearchOrchestraPresenter : BasePresenter<SearchOrchestraView>() {
    private var interactor: SearchOrchestraInteractor? = null

    override fun attachView(view: SearchOrchestraView) {
        super.attachView(view)
        interactor = SearchOrchestraInteractor()
    }

    override fun detachView() {
        interactor = null
        super.detachView()
    }

    fun addFavourite(favouriteEntity: FavouriteEntity) {
        ifViewAttached {
            getToken().flatMap { token ->
                interactor?.addFavourite(compositeDisposable, favouriteEntity, token)
            }.subscribe({
            }, {
                showErrorView(it) {}
            }).let {
                compositeDisposable?.add(it)
            }
        }
    }
}