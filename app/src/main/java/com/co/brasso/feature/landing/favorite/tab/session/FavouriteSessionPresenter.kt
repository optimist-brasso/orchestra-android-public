package com.co.brasso.feature.landing.favorite.tab.session

import com.co.brasso.R
import com.co.brasso.feature.shared.base.BasePresenter
import com.co.brasso.feature.shared.model.favourite.FavouriteSessionEntity

class FavouriteSessionPresenter : BasePresenter<FavouriteSessionView>() {
    private var favouriteInteractor: FavouriteSessionInteractor? = null

    override fun attachView(view: FavouriteSessionView) {
        super.attachView(view)
        favouriteInteractor = FavouriteSessionInteractor()
    }

    override fun detachView() {
        favouriteInteractor = null
        super.detachView()
    }

    fun getMySessionFavouriteList(searchSlug: String?, page: Int?, isPullToRefresh: Boolean) {
        ifViewAttached { view ->
            if (view.isNetworkAvailable()) {
                if (isPullToRefresh) {
                    if (page == 1) {
                        view.showLoading()
                    }
                } else
                    if (page == 1) {
                        view.showProgressBar()
                    }
                getToken().flatMap {
                    favouriteInteractor?.getMySessionFavouriteList(
                        compositeDisposable,
                        it,
                        searchSlug, page
                    )
                }.subscribe({
                    if (isPullToRefresh) {
                        if (page == 1) {
                            view.hideLoading()
                        }
                    } else {
                        if (page == 1) {
                            view.hideProgressBar()
                        }
                    }
                    view.hideProgressBar()
                    view.setAdapter(it.toMutableList())
                }, {
                    if (isPullToRefresh) {
                        if (page == 1) {
                            view.hideLoading()
                        }
                    }
                    else {
                        if (page == 1) {
                            view.hideProgressBar()
                        }
                    }
                    showErrorView(it) {}
                }).let {
                    compositeDisposable?.add(it)
                }
            } else
                view.showMessageDialog(R.string.error_no_internet_connection)
        }
    }

    fun addSessionFavourite(favouriteEntity: FavouriteSessionEntity?) {
        ifViewAttached {
            getToken().flatMap { token ->
                favouriteInteractor?.addFavourite(compositeDisposable, favouriteEntity, token)
            }.subscribe({
            }, {
                showErrorView(it) {}
            }).let {
                compositeDisposable?.add(it)
            }
        }
    }
}