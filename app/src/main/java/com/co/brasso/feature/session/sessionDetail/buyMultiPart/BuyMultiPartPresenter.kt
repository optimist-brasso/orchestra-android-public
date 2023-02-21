package com.co.brasso.feature.session.sessionDetail.buyMultiPart

import com.co.brasso.R
import com.co.brasso.feature.shared.base.BasePresenter
import com.co.brasso.feature.shared.model.request.BoughtProducts
import com.co.brasso.feature.shared.model.request.cart.AddToCart
import com.co.brasso.feature.shared.model.request.cart.AddToCartList
import com.co.brasso.feature.shared.model.response.cartList.CartListResponse
import com.co.brasso.feature.shared.model.response.multiPartList.MultiPartListResponseItem
import com.co.brasso.feature.shared.model.response.sessionlayout.InstrumentResponse

class BuyMultiPartPresenter : BasePresenter<BuyMultiPartView>() {
    private var buyMultiPartInteractor: BuyMultiPartInteractor? = null

    override fun attachView(view: BuyMultiPartView) {
        super.attachView(view)
        buyMultiPartInteractor = BuyMultiPartInteractor()
    }

    override fun detachView() {
        buyMultiPartInteractor = null
        super.detachView()
    }

    fun getMultiPartList(sessionID: Int?) {
        ifViewAttached { view ->
            if (view.isNetworkAvailable()) {
                view.showProgressBar()
                getToken().flatMap {
                    buyMultiPartInteractor?.getMultiPartList(compositeDisposable, sessionID, it)
                }.subscribe({
                    view.hideProgressBar()
                    view.success(it)
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

    fun addToCart(addToCart: AddToCartList) {
        ifViewAttached { view ->
            if (!view.isNetworkAvailable()) {
                view.showMessageDialog(R.string.error_no_internet_connection)
                return@ifViewAttached
            }
            if (!validateCartItem(view, addToCart.orchestraItems))
                return@ifViewAttached
            view.showLoading()
            getToken().flatMap { token ->
                buyMultiPartInteractor?.addToCart(compositeDisposable, addToCart, token)
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

    private fun validateCartItem(
        view: BuyMultiPartView,
        orchestraItems: List<AddToCart>?
    ): Boolean {
        if (orchestraItems.isNullOrEmpty()) {
            view.showMessageDialog(R.string.text_select_one_instrument)
            return false
        }
        return true
    }

    fun getPartFilterList(
        multiPartListResponse: List<MultiPartListResponseItem>?,
        instrumentList: List<InstrumentResponse>?,
        fromPage: String?
    ): MutableList<MultiPartListResponseItem> {
        val partList: MutableList<MultiPartListResponseItem> = mutableListOf()
        instrumentList?.forEach { instrument ->
            val part =
                multiPartListResponse?.find { it.player?.id == instrument.musicianId }
            part?.fromPage = fromPage
            if (part != null) {
                partList.add(part)
            }
        }

        return partList
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
                    buyMultiPartInteractor?.purchaseCartItem(
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
                buyMultiPartInteractor?.purchaseVerify(compositeDisposable, boughtProducts, token)
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