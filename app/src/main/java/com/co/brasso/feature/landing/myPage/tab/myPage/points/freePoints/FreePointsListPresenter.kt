package com.co.brasso.feature.landing.myPage.tab.myPage.points.freePoints

import com.co.brasso.R
import com.co.brasso.feature.shared.base.BasePresenter

class FreePointsListPresenter : BasePresenter<FreePointsListView>() {
    private var bundleListInteractor: FreePointsListInteractor? = null

    override fun attachView(view: FreePointsListView) {
        super.attachView(view)
        bundleListInteractor = FreePointsListInteractor()
    }

    override fun detachView() {
        bundleListInteractor = null
        super.detachView()
    }

    fun getFreeBundleList(page: Int?, isPullToRefresh: Boolean) {
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
                    bundleListInteractor?.getFreeBundleList(
                        compositeDisposable,
                        it,
                        page
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
                    view.setFreeBundleList(it.toMutableList())
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
}