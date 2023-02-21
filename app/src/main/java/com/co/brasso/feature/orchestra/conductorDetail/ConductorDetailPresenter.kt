package com.co.brasso.feature.orchestra.conductorDetail

import com.co.brasso.R
import com.co.brasso.feature.download.DownloadPresenter
import com.co.brasso.feature.shared.model.favourite.FavouriteEntity
import com.co.brasso.utils.extension.getSubscription

class ConductorDetailPresenter : DownloadPresenter<ConductorDetailView>() {
    private lateinit var conductorDetailInteractor: ConductorDetailInteractor

    override fun attachView(view: ConductorDetailView) {
        super.attachView(view)
        conductorDetailInteractor = ConductorDetailInteractor()
    }

    fun getOrchestraDetail(id: Int?, videoSupport: String?) {
        ifViewAttached { view ->
            if (!view.isNetworkAvailable()) {
                view.hideProgressBar()
                view.noInternet(R.string.error_no_internet_connection)
                return@ifViewAttached
            }
            view.showProgressBar()
            getToken().flatMap { token ->
                conductorDetailInteractor.getOrchestraDetail(
                    compositeDisposable,
                    token,
                    videoSupport,
                    id
                )
            }.getSubscription()?.subscribe({
                view.hideProgressBar()
                view.setConductorDetail(it)
                setOrchestraData(it, it.iosVrFile)
                getDownloadedFilePath()
            }, {
                view.hideProgressBar()
                showErrorView(it) {}
            })?.let {
                compositeDisposable?.add(it)
            }
        }
    }

    fun addFavourite(favouriteEntity: FavouriteEntity?) {
        ifViewAttached { view ->
            if (!view.isNetworkAvailable()) {
                view.showMessageDialog(R.string.error_no_internet_connection)
                return@ifViewAttached
            }

            getToken().flatMap { token ->
                conductorDetailInteractor.addFavourite(
                    compositeDisposable,
                    favouriteEntity,
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