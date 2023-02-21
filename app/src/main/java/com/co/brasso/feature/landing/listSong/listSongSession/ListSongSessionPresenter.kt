package com.co.brasso.feature.landing.listSong.listSongSession

import com.co.brasso.R
import com.co.brasso.feature.shared.base.BasePresenter
import com.co.brasso.feature.shared.model.favourite.FavouriteSessionEntity

class ListSongSessionPresenter : BasePresenter<ListSongSessionView>() {
    private var interactor: ListSongSessionInteractor? = null

    override fun attachView(view: ListSongSessionView) {
        super.attachView(view)
        interactor = ListSongSessionInteractor()
    }

    override fun detachView() {
        interactor = null
        super.detachView()
    }

    fun orchestraSearch(slug: String?, page: Int?, isSwipeToRefresh: Boolean) {
        ifViewAttached { view ->
            if (view.isNetworkAvailable()) {
                if (isSwipeToRefresh) {
                    if (page == 1) {
                        view.showLoading()
                    }
                } else
                    if (page == 1) {
                        view.showProgressBar()
                    }
                getToken().flatMap {
                    interactor?.getSessionSearch(it, slug, page, compositeDisposable)
                }.subscribe({
                    if (isSwipeToRefresh) {
                        if (page == 1) {
                            view.hideLoading()
                        }
                    } else {
                        if (page == 1) {
                            view.hideProgressBar()
                        }
                    }
                    view.setAdapter(it)
                }, {
                    if (isSwipeToRefresh) {
                        if (page == 1) {
                            view.hideLoading()
                        }
                    } else {
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