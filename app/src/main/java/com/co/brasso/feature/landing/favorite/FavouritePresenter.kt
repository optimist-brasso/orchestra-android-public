package com.co.brasso.feature.landing.favorite

import com.co.brasso.R
import com.co.brasso.feature.shared.base.BasePresenter


class FavouritePresenter : BasePresenter<FavouriteView>() {
    private var favouriteInteractor: FavouriteInteractor? = null

    override fun attachView(view: FavouriteView) {
        super.attachView(view)
        favouriteInteractor = FavouriteInteractor()
    }

    override fun detachView() {
        favouriteInteractor = null
        super.detachView()
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
                    view.setFavouriteList(it)
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