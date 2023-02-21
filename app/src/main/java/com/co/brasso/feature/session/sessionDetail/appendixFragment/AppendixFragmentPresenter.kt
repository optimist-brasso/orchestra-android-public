package com.co.brasso.feature.session.sessionDetail.appendixFragment

import com.co.brasso.R
import com.co.brasso.feature.download.DownloadPresenter
import com.co.brasso.feature.shared.model.favourite.FavouriteSessionEntity
import com.co.brasso.feature.shared.model.request.BoughtProducts
import com.co.brasso.feature.shared.model.request.cart.AddToCartList
import com.co.brasso.feature.shared.model.response.cartList.CartListResponse

class AppendixFragmentPresenter : DownloadPresenter<AppendixFragmentView>() {
    private var appendixFragmentInteractor: AppendixFragmentInteractor? = null

    override fun attachView(view: AppendixFragmentView) {
        super.attachView(view)
        appendixFragmentInteractor = AppendixFragmentInteractor()
    }

    override fun detachView() {
        appendixFragmentInteractor = null
        super.detachView()
    }

    fun getInstrumentDetail(
        instrumentID: Int?,
        sessionID: Int?,
        musicianId: Int?,
        videoSupport: String?
    ) {
        ifViewAttached { view ->
            if (!view.isNetworkAvailable()) {
                view.hideProgressBar()
                view.noInternet(R.string.error_no_internet_connection)
                return@ifViewAttached
            }

            view.showProgressBar()
            getToken().flatMap {
                appendixFragmentInteractor?.getInstrumentDetail(
                    compositeDisposable,
                    instrumentID,
                    sessionID,
                    musicianId,
                    it,
                    videoSupport
                )
            }.subscribe({
                view.hideProgressBar()
                view.getInstrumentSuccess(it)
                setOrchestraData(it.hallSoundDetail, it.premiumFile)
                getDownloadedFilePath()
            }, {
                view.hideProgressBar()
                showErrorView(it) {}
            }).let {
                compositeDisposable?.add(it)
            }
        }
    }

    fun addToCart(addToCart: AddToCartList) {
        ifViewAttached { view ->
            if (!view.isNetworkAvailable()) {
                view.showMessageDialog(R.string.error_no_internet_connection)
                return@ifViewAttached
            }

            view.showLoading()
            getToken().flatMap { token ->
                appendixFragmentInteractor?.addToCart(compositeDisposable, addToCart, token)
            }.subscribe({
                view.hideLoading()
                view.addToCartSuccess(it)
            }, {
                view.hideLoading()
                showErrorView(it) {}
            }).let {
                compositeDisposable?.add(it)
            }
        }
    }


    fun buyOrchestra(purchasedItemsList: MutableList<CartListResponse>?) {
        ifViewAttached { view ->
            if (!view.isNetworkAvailable()) {
                view.showMessageDialog(R.string.error_no_internet_connection)
                return@ifViewAttached
            }

            if (purchasedItemsList.isNullOrEmpty()) {
                view.showMessageDialog(R.string.purchase_item_empty)
                return@ifViewAttached
            }
            view.showLoading()
            getToken().flatMap {
                appendixFragmentInteractor?.purchaseCartItem(
                    compositeDisposable,
                    it,
                    purchasedItemsList
                )
            }.subscribe({
                view.hideLoading()
                view.purchaseSuccess(it.message)
            }, {
                view.hideLoading()
                showErrorView(it) {}
            }).let {
                compositeDisposable?.add(it)
            }
        }
    }

    fun purchaseVerify(boughtProducts: BoughtProducts?) {
        ifViewAttached { view ->
            if (!view.isNetworkAvailable()) {
                view.showMessageDialog(R.string.error_no_internet_connection)
                return@ifViewAttached
            }

            view.showLoading()
            getToken().flatMap { token ->
                appendixFragmentInteractor?.purchaseVerify(
                    compositeDisposable,
                    boughtProducts,
                    token
                )
            }.subscribe({
                view.hideLoading()
                view.purchaseSuccess(it.message)
            }, {
                view.hideLoading()
                showErrorView(it) {}
            }).let {
                compositeDisposable?.add(it)
            }
        }
    }

    fun addFavourite(favouriteEntity: FavouriteSessionEntity?) {
        ifViewAttached { view ->
            if (!view.isNetworkAvailable()) {
                view.showMessageDialog(R.string.error_no_internet_connection)
                return@ifViewAttached
            }
            getToken().flatMap { token ->
                appendixFragmentInteractor?.addFavourite(
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