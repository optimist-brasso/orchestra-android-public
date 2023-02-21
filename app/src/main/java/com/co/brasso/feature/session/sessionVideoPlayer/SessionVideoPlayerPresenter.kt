package com.co.brasso.feature.session.sessionVideoPlayer

import com.co.brasso.R
import com.co.brasso.feature.shared.base.BasePresenter
import com.co.brasso.feature.shared.model.recording.RecordingEntity
import com.co.brasso.feature.shared.model.request.BoughtProducts
import com.co.brasso.feature.shared.model.request.cart.AddToCartList
import com.co.brasso.feature.shared.model.response.cartList.CartListResponse

class SessionVideoPlayerPresenter : BasePresenter<SessionVideoPlayerView>() {
    private var sessionVideoPlayerInteractor: SessionVideoPlayerInteractor? = null

    override fun attachView(view: SessionVideoPlayerView) {
        super.attachView(view)
        sessionVideoPlayerInteractor = SessionVideoPlayerInteractor()
    }

    override fun detachView() {
        sessionVideoPlayerInteractor = null
        super.detachView()
    }

    fun saveRecordingFile(recordingEntity: RecordingEntity) {
        ifViewAttached { view ->
            if (view.isNetworkAvailable()) {
                view.showLoading()
                getToken().flatMap {
                    sessionVideoPlayerInteractor?.saveRecording(
                        compositeDisposable,
                        recordingEntity,
                        it
                    )
                }.subscribe({
                    view.hideLoading()
                    view.success(it.message)
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

    fun addToCart(addToCart: AddToCartList) {
        ifViewAttached { view ->
            view.showLoading()
            getToken().flatMap { token ->
                sessionVideoPlayerInteractor?.addToCart(compositeDisposable, addToCart, token)
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
                    sessionVideoPlayerInteractor?.purchaseCartItem(
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
            } else
                view.showMessageDialog(R.string.error_no_internet_connection)
        }
    }

    fun purchaseVerify(boughtProducts: BoughtProducts?) {
        ifViewAttached { view ->
            view.showLoading()
            getToken().flatMap { token ->
                sessionVideoPlayerInteractor?.purchaseVerify(compositeDisposable, boughtProducts, token)
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
}