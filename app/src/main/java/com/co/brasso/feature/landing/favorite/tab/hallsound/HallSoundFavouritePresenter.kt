package com.co.brasso.feature.landing.favorite.tab.hallsound

import com.co.brasso.R
import com.co.brasso.feature.landing.favorite.FavouriteInteractor
import com.co.brasso.feature.shared.base.BasePresenter
import com.co.brasso.feature.shared.model.favourite.FavouriteEntity

class HallSoundFavouritePresenter : BasePresenter<HallSoundFavouriteView>() {
    private var hallSoundFavouriteInteractor: HallSoundFavouriteInteractor? = null
    private var favouriteInteractor: FavouriteInteractor? = null

    override fun attachView(view: HallSoundFavouriteView) {
        super.attachView(view)
        hallSoundFavouriteInteractor = HallSoundFavouriteInteractor()
        favouriteInteractor = FavouriteInteractor()
    }

    override fun detachView() {
        hallSoundFavouriteInteractor = null
        favouriteInteractor = null
        super.detachView()
    }

    fun addFavourite(favouriteEntity: FavouriteEntity?) {
        ifViewAttached {
            getToken().flatMap { token ->
                hallSoundFavouriteInteractor?.addFavourite(compositeDisposable, favouriteEntity, token)
            }.subscribe({
            }, {
                showErrorView(it) {}
            }).let {
                compositeDisposable?.add(it)
            }
        }
    }

    fun getFavouriteList(searchSlug: String?, isPullToRefresh: Boolean) {
        ifViewAttached { view ->
            if (view.isNetworkAvailable()) {
                if (isPullToRefresh)
                    view.showLoading()
                else
                    view.showProgressBar()
                getToken().flatMap {
                    favouriteInteractor?.getFavouriteList(compositeDisposable,searchSlug , it)
                }.subscribe({
                    if (isPullToRefresh)
                        view.hideLoading()
                    else
                        view.hideProgressBar()
                    view.hideProgressBar()
                    view.setSearchData(it)
                }, {
                    if (isPullToRefresh)
                        view.hideLoading()
                    else
                        view.hideProgressBar()
                    showErrorView(it) {}
                }).let {
                    compositeDisposable?.add(it)
                }
            } else
                view.showMessageDialog(R.string.error_no_internet_connection)
        }
    }

}