package com.co.brasso.feature.player

import com.co.brasso.R
import com.co.brasso.feature.landing.myPage.tab.myPage.points.PointsListInteractor
import com.co.brasso.feature.shared.base.BasePresenter

class PlayerListFragmentPresenter : BasePresenter<PlayerListFragmentView>() {
    private var playerListFragmentInteractor: PlayerListFragmentInteractor? = null
    private var bundleListInteractor: PointsListInteractor? = null

    override fun attachView(viewList: PlayerListFragmentView) {
        super.attachView(viewList)
        playerListFragmentInteractor = PlayerListFragmentInteractor()
        bundleListInteractor = PointsListInteractor()
    }

    override fun detachView() {
        super.detachView()
        playerListFragmentInteractor = null
        bundleListInteractor = null
    }

    fun getPlayerList(slug: String?, page: Int?) {
        ifViewAttached { view ->
            if (view.isNetworkAvailable()) {
                if (page == 1)
                    view.showProgressBar()
                playerListFragmentInteractor?.getPlayerList(compositeDisposable, slug, page)
                    ?.subscribe({
                        if (page == 1)
                            view.hideProgressBar()
                        view.success(it.toMutableList())
                    }, {
                        if (page == 1)
                            view.hideProgressBar()
                        showErrorView(it) {}
                    })?.let {
                        compositeDisposable?.add(it)
                    }
            } else
                view.showMessageDialog(R.string.error_no_internet_connection)
        }
    }

    fun getPlayerListForPullToRefresh(slug: String?, page: Int?) {
        ifViewAttached { view ->
            if (view.isNetworkAvailable()) {
                view.showLoading()
                playerListFragmentInteractor?.getPlayerList(compositeDisposable, slug, page)
                    ?.subscribe({
                        view.hideLoading()
                        view.success(it.toMutableList())
                    }, {
                        view.hideLoading()
                        showErrorView(it) {}
                    })?.let {
                        compositeDisposable?.add(it)
                    }
            } else
                view.showMessageDialog(R.string.error_no_internet_connection)
        }
    }

    fun getBundleList() {
        ifViewAttached { view ->
            if (view.isNetworkAvailable()) {
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
}