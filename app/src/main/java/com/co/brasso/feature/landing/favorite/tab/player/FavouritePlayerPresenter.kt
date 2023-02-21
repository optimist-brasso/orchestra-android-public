package com.co.brasso.feature.landing.favorite.tab.player

import com.co.brasso.R
import com.co.brasso.feature.shared.base.BasePresenter
import com.co.brasso.feature.shared.model.favourite.FavouriteMusician

class FavouritePlayerPresenter : BasePresenter<FavouritePlayerView>() {

    private var favouritePlayerInteractor: FavouritePlayerInteractor? = null

    override fun attachView(view: FavouritePlayerView) {
        super.attachView(view)
        favouritePlayerInteractor = FavouritePlayerInteractor()
    }

    override fun detachView() {
        favouritePlayerInteractor = null
        super.detachView()
    }

    fun getMyPlayerFavouriteList(slug: String?, page: Int?) {
        ifViewAttached { view ->
            if (view.isNetworkAvailable()) {
                if (page == 1)
                    view.showProgressBar()
                getToken().flatMap {
                    favouritePlayerInteractor?.getMyPlayerFavouriteList(
                        compositeDisposable,
                        it,
                        slug,
                        page
                    )
                }.subscribe({
                    if (page == 1)
                        view.hideProgressBar()
                    view.setMyPlayerFavourite(it)
                }, {
                    if (page == 1)
                        view.hideProgressBar()
                    showErrorView(it) {}
                }).let {
                    compositeDisposable?.add(it)
                }
            } else
                view.showMessageDialog(R.string.error_no_internet_connection)
        }
    }

    fun getMyPlayerFavouriteListOnPullToRefresh(slug: String?, page: Int?) {
        ifViewAttached { view ->
            if (view.isNetworkAvailable()) {
                view.showLoading()
                getToken().flatMap {
                    favouritePlayerInteractor?.getMyPlayerFavouriteList(
                        compositeDisposable,
                        it,
                        slug,
                        page
                    )
                }.subscribe({
                    view.hideLoading()
                    view.setMyPlayerFavourite(it)
                }, {
                    view.hideLoading()
                    showErrorView(it) {}
                }).let {
                    compositeDisposable?.add(it)
                }
            } else
                view.showMessageDialog(R.string.error_no_internet_connection)
        }
    }

    fun addMusicianFavourite(favouriteMusician: FavouriteMusician?) {
        ifViewAttached {
            getToken().flatMap { token ->
                favouritePlayerInteractor?.favouriteMusician(
                    compositeDisposable,
                    favouriteMusician,
                    token
                )
            }.subscribe({
            }, {
                showErrorView(it) {}
            }).let {
                compositeDisposable?.add(it)
            }
        }
    }
}