package com.co.brasso.feature.hallSound.hallSoundDetail

import com.co.brasso.R
import com.co.brasso.feature.cart.CartListInteractor
import com.co.brasso.feature.download.DownloadPresenter
import com.co.brasso.feature.shared.model.favourite.FavouriteEntity
import com.co.brasso.feature.shared.model.request.BoughtProducts
import com.co.brasso.feature.shared.model.request.cart.AddToCartList
import com.co.brasso.feature.shared.model.response.cartList.CartListResponse
import com.co.brasso.feature.shared.model.response.hallsound.HallSoundAudio

class HallSoundDetailPresenter : DownloadPresenter<HallSoundDetailView>() {
    private var interactor: HallSoundDetailInteractor? = null
    private var cartListInteractor: CartListInteractor? = null

    override fun attachView(view: HallSoundDetailView) {
        super.attachView(view)
        interactor = HallSoundDetailInteractor()
        cartListInteractor = CartListInteractor()
    }

    override fun detachView() {
        interactor = null
        cartListInteractor = null
        super.detachView()
    }

    fun getOrchestraDetail(id: Int?, videoSupport:String?) {
        ifViewAttached { view ->
            if (!view.isNetworkAvailable()) {
                view.hideProgressBar()
                view.noInternet(R.string.error_no_internet_connection)
                return@ifViewAttached
            }
            view.showProgressBar()
            getToken().flatMap { token ->
                interactor?.getHallSoundDetail(compositeDisposable, token, id,videoSupport)
            }.subscribe({
                view.hideProgressBar()
                it?.hallSound?.sortBy { it.position }
                setOrchestraHallSoundData(it)
                view.setHallSoundDetail(it)
                getDownloadedFilePath()
            }, {
                view.hideProgressBar()
                showErrorView(it) {}
            }).let {
                compositeDisposable?.add(it)
            }
        }
    }

    fun addFavourite(favouriteEntity: FavouriteEntity?) {
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

    fun addToCart(addToCart: AddToCartList) {
        ifViewAttached { view ->
            if (!view.isNetworkAvailable()) {
                view.showMessageDialog(R.string.error_no_internet_connection)
                return@ifViewAttached
            }
            view.showLoading()
            getToken().flatMap { token ->
                interactor?.addToCart(compositeDisposable, addToCart, token)
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
            if (view.isNetworkAvailable()) {
                if (purchasedItemsList.isNullOrEmpty()) {
                    view.showMessageDialog(R.string.purchase_item_empty)
                    return@ifViewAttached
                }
                view.showLoading()
                getToken().flatMap {
                    cartListInteractor?.purchaseCartItem(
                        compositeDisposable,
                        it,
                        purchasedItemsList
                    )
                }.subscribe({
                    view.hideLoading()
                    view.purchaseSuccess(it.message, true)
                }, {
                    view.hideLoading()
                    showErrorView(it) {}
                }).let {
                    compositeDisposable?.add(it)
                }
            } else
                view.showMessageDialog(R.string.error_no_internet_connection)
        }
    }

    fun purchaseVerify(boughtProducts: BoughtProducts?) {
        ifViewAttached { view ->
            view.showLoading()
            getToken().flatMap { token ->
                interactor?.purchaseVerify(compositeDisposable, boughtProducts, token)
            }.subscribe({
                view.hideLoading()
                view.purchaseSuccess(it.message, true)
            }, {
                view.hideLoading()
                showErrorView(it) {}
            }).let {
                compositeDisposable?.add(it)
            }
        }
    }

}