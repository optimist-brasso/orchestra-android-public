package com.co.brasso.feature.landing

import com.co.brasso.R
import com.co.brasso.feature.cart.CartListInteractor
import com.co.brasso.feature.shared.base.BasePresenter
import com.co.brasso.feature.shared.model.response.appinfo.AppInfoGlobal
import com.co.brasso.feature.shared.model.response.notification.NotificationResponse

class LandingPresenter : BasePresenter<LandingView>() {

    private var cartListInteractor: CartListInteractor? = null

    override fun attachView(view: LandingView) {
        super.attachView(view)
        cartListInteractor = CartListInteractor()
    }

    override fun detachView() {
        cartListInteractor = null
        super.detachView()
    }

    fun getNotifications() {
        ifViewAttached { view ->
            cartListInteractor?.getAllNotifications(compositeDisposable, view.getAppDatabase())
                ?.subscribe({
                    view.setNotificationCount(it as MutableList<NotificationResponse>?)
                }, {
                })?.let {
                    compositeDisposable?.add(it)
                }
        }
    }

    fun getCartItem() {
        ifViewAttached { view ->
            if (view.isNetworkAvailable()) {
                getToken().flatMap {
                    cartListInteractor?.getCartList(compositeDisposable, it)
                }.subscribe({ cartInfo ->
                    AppInfoGlobal.cartInfo = cartInfo.toMutableList()
                    view.showCartCount()
                }, {
                    showErrorView(it) {}
                }).let {
                    compositeDisposable?.add(it)
                }
            } else
                view.showMessageDialog(R.string.error_no_internet_connection)
        }
    }
}