package com.co.brasso.feature.session.sessionDetail.sessionDetailPremium

import com.co.brasso.R
import com.co.brasso.feature.download.DownloadPresenter
import com.co.brasso.feature.shared.model.favourite.FavouriteSessionEntity
import com.co.brasso.feature.shared.model.request.BoughtProducts
import com.co.brasso.feature.shared.model.request.cart.AddToCartList
import com.co.brasso.feature.shared.model.response.cartList.CartListResponse

class SessionDetailPremiumPresenter : DownloadPresenter<SessionDetailPremiumView>() {
    private var sessionDetailPremiumInteractor: SessionDetailPremiumInteractor? = null

    override fun attachView(view: SessionDetailPremiumView) {
        super.attachView(view)
        sessionDetailPremiumInteractor = SessionDetailPremiumInteractor()
    }

    override fun detachView() {
        sessionDetailPremiumInteractor = null
        super.detachView()
    }

    fun getInstrumentDetail(instrumentID: Int?, sessionID: Int?, musicianId: Int?,videoSupport:String?) {
        ifViewAttached { view ->
            if (!view.isNetworkAvailable()) {
                view.hideProgressBar()
                view.noInternet(R.string.error_no_internet_connection)
                return@ifViewAttached
            }

            view.showProgressBar()
            getToken().flatMap {
                sessionDetailPremiumInteractor?.getInstrumentDetail(
                    compositeDisposable,
                    instrumentID,
                    sessionID,
                    musicianId,
                    it,
                    videoSupport
                )
            }.subscribe({
                view.hideProgressBar()
                view.success(it)
                it.hallSoundDetail?.leftViewAngle=it.leftViewAngle
                it.hallSoundDetail?.rightViewAngle=it.rightViewAngle
                setOrchestraData(it.hallSoundDetail, it.iosPremiumVrFile)
                getDownloadedFilePath()
            }, {
                view.hideProgressBar()
                showErrorView(it) {

                }
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
                sessionDetailPremiumInteractor?.addToCart(compositeDisposable, addToCart, token)
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
                sessionDetailPremiumInteractor?.purchaseCartItem(
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
                sessionDetailPremiumInteractor?.purchaseVerify(
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
                sessionDetailPremiumInteractor?.addFavourite(
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