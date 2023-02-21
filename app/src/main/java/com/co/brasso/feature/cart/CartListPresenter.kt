package com.co.brasso.feature.cart

import com.co.brasso.R
import com.co.brasso.feature.shared.base.BasePresenter
import com.co.brasso.feature.shared.model.request.BoughtProducts
import com.co.brasso.feature.shared.model.response.appinfo.AppInfoGlobal
import com.co.brasso.feature.shared.model.response.cartList.CartListResponse

class CartListPresenter : BasePresenter<CartListView>() {
    private var cartListInteractor: CartListInteractor? = null

    override fun attachView(view: CartListView) {
        super.attachView(view)
        cartListInteractor = CartListInteractor()
    }

    override fun detachView() {
        cartListInteractor = null
        super.detachView()
    }

    fun getCartList(isSwipeToRefresh: Boolean) {
        ifViewAttached { view ->
            if (view.isNetworkAvailable()) {
                if (isSwipeToRefresh)
                    view.showLoading()
                else
                    view.showProgressBar()
                getToken().flatMap {
                    cartListInteractor?.getCartList(compositeDisposable, it)
                }.subscribe({
                    if (isSwipeToRefresh)
                        view.hideLoading()
                    else
                        view.hideProgressBar()
                    AppInfoGlobal.cartInfo = it as MutableList<CartListResponse>?
                    view.setCartList(it)
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

    fun deleteCartItem(id: Int?) {
        ifViewAttached { view ->
            if (view.isNetworkAvailable()) {
                view.showProgressBar()
                getToken().flatMap {
                    cartListInteractor?.deleteCartItem(compositeDisposable, it, id)
                }.subscribe({
                    view.hideProgressBar()
                    view.deleteSuccess(it.message, id)
                }, {
                    view.hideProgressBar()
                    showErrorView(it) {}
                }).let {
                    compositeDisposable?.add(it)
                }
            } else
                view.showMessageDialog(R.string.error_no_internet_connection)
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
                cartListInteractor?.purchaseVerify(compositeDisposable, boughtProducts, token)
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