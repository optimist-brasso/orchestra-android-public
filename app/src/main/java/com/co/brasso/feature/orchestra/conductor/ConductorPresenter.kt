package com.co.brasso.feature.orchestra.conductor

import com.co.brasso.R
import com.co.brasso.feature.landing.myPage.tab.myPage.points.PointsListInteractor
import com.co.brasso.feature.landing.search.SearchInteractor
import com.co.brasso.feature.shared.base.BasePresenter
import com.co.brasso.feature.shared.model.favourite.FavouriteEntity


class ConductorPresenter : BasePresenter<ConductorView>() {
    private var interactor: ConductorInteractor? = null
    private var searchInteractor: SearchInteractor? = null
    private var bundleListInteractor: PointsListInteractor? = null

    override fun attachView(view: ConductorView) {
        super.attachView(view)
        interactor = ConductorInteractor()
        searchInteractor = SearchInteractor()
        bundleListInteractor = PointsListInteractor()
    }

    override fun detachView() {
        super.detachView()
        interactor = null
        searchInteractor = null
        bundleListInteractor = null
    }

    fun getOrchestras(isSwipeToRefresh: Boolean) {
        ifViewAttached { view ->
            if (isSwipeToRefresh) view.showLoading()
            else view.showProgressBar()
            getToken().flatMap { token ->
                interactor?.getOrchestra(compositeDisposable, token)
            }.subscribe({
                if (isSwipeToRefresh) view.hideLoading()
                else view.hideProgressBar()
                view.setOrchestraList(it.toMutableList())
            }, {
                if (isSwipeToRefresh) view.hideLoading()
                else view.hideProgressBar()
                showErrorView(it) {}
            }).let {
                compositeDisposable?.add(it)
            }
        }

    }


    fun orchestraSearch(slug: String?, isSwipeToRefresh: Boolean) {
        ifViewAttached { view ->
            if (view.isNetworkAvailable()) {
                if (isSwipeToRefresh) view.showLoading()
                else view.showProgressBar()
                getToken().flatMap {
                    searchInteractor?.orchestraSearch(it, slug, compositeDisposable)
                }.subscribe({
                    if (isSwipeToRefresh) view.hideLoading()
                    else view.hideProgressBar()
                    view.setOrchestraList(it)
                }, {
                    if (isSwipeToRefresh) view.hideLoading()
                    else view.hideProgressBar()
                    showErrorView(it) {}
                }).let {
                    compositeDisposable?.add(it)
                }
            } else view.showMessageDialog(R.string.error_no_internet_connection)
        }
    }

    fun addFavourite(favouriteEntity: FavouriteEntity?) {
        ifViewAttached {
            getToken().flatMap { token ->
                interactor?.addFavourite(compositeDisposable, favouriteEntity, token)
            }.subscribe({}, {
                showErrorView(it) {}
            }).let {
                compositeDisposable?.add(it)
            }
        }
    }

    fun getBundleList() {
        ifViewAttached { view ->
            getToken().flatMap { token ->
                bundleListInteractor?.getBundleList(compositeDisposable, token)
            }.subscribe({
                view.setBundleList(it)
            }, {
                showErrorView(it) {}
            }).let {
                compositeDisposable?.add(it)
            }
        }
    }
}
