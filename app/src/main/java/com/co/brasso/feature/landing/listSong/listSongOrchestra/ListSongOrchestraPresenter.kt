package com.co.brasso.feature.landing.listSong.listSongOrchestra

import com.co.brasso.R
import com.co.brasso.feature.landing.search.SearchInteractor
import com.co.brasso.feature.shared.base.BasePresenter
import com.co.brasso.feature.shared.model.favourite.FavouriteEntity

class ListSongOrchestraPresenter : BasePresenter<ListSongOrchestraView>() {
    private var interactor: ListSongOrchestraInteractor? = null
    private var searchInteractor: SearchInteractor? = null

    override fun attachView(view: ListSongOrchestraView) {
        super.attachView(view)
        interactor = ListSongOrchestraInteractor()
        searchInteractor = SearchInteractor()
    }

    override fun detachView() {
        interactor = null
        searchInteractor = null
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

    fun orchestraSearch(slug: String?, isSwipeToRefresh: Boolean) {
        ifViewAttached { view ->
            if (view.isNetworkAvailable()) {
                if (isSwipeToRefresh)
                    view.showLoading()
                else
                    view.showProgressBar()
                getToken().flatMap {
                    searchInteractor?.orchestraSearch(it, slug, compositeDisposable)
                }.subscribe({
                    if (isSwipeToRefresh)
                        view.hideLoading()
                    else
                        view.hideProgressBar()
                    view.setSearchData(it)
                }, {
                    if (isSwipeToRefresh)
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