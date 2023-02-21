package com.co.brasso.feature.landing.myPage.tab.myPage.points

import android.app.Activity
import com.android.billingclient.api.BillingClient
import com.android.billingclient.api.Purchase
import com.co.brasso.R
import com.co.brasso.feature.shared.base.BasePresenter
import com.co.brasso.feature.shared.model.request.BoughtProducts

class PointsListPresenter : BasePresenter<PointsListView>() {
    private var bundleListInteractor: PointsListInteractor? = null

    override fun attachView(view: PointsListView) {
        super.attachView(view)
        bundleListInteractor = PointsListInteractor()
    }

    override fun detachView() {
        bundleListInteractor = null
        super.detachView()
    }

    fun getBundleList(isPullToRefresh: Boolean) {
        ifViewAttached { view ->
            if (view.isNetworkAvailable()) {
                if (isPullToRefresh)
                    view.showLoading()
                else
                    view.showProgressBar()
                getToken().flatMap { token ->
                    bundleListInteractor?.getBundleList(compositeDisposable, token)
                }.subscribe({
                    if (isPullToRefresh)
                        view.hideLoading()
                    else
                        view.hideProgressBar()
                    view.setBundleList(it)
                }, {
                    if (isPullToRefresh)
                        view.hideLoading()
                    else
                        view.hideProgressBar()
                    showErrorView(it) {}
                }).let {
                    compositeDisposable?.add(it)
                }
            }
        }
    }

    fun purchaseVerify(
        context: Activity?,
        billingClient: BillingClient?,
        boughtProducts: BoughtProducts?,
        purchase: Purchase
    ) {
        ifViewAttached { view ->
            view.showLoading()
            getToken().flatMap { token ->
                bundleListInteractor?.purchaseVerify(compositeDisposable, boughtProducts, token)
            }.subscribe({
                handlePurchase(context,billingClient, purchase, it.message)
                        }, {
                view.hideLoading()
                showErrorView(it) {}
            }).let {
                compositeDisposable?.add(it)
            }
        }
    }

    private fun handlePurchase(
        context: Activity?,
        billingClient: BillingClient?,
        purchase: Purchase,
        message: String?
    ) {
        ifViewAttached { view ->
            bundleListInteractor?.enableConsumableProduct(purchase, billingClient)?.subscribe({
                context?.runOnUiThread {
                    view.hideLoading()
                    view.purchaseSuccess(message)
                }
            }, {
                context?.runOnUiThread {
                    view.hideLoading()
                    showErrorView(it) {}
                }
            })?.let {
                compositeDisposable?.add(it)
            }
        }
    }

    fun checkProductAvailability(
        context: Activity?,
        bundleIdentifier: String?,
        billingClient: BillingClient?
    ) {
        ifViewAttached { view ->
            if (view.isNetworkAvailable()) {
                view.showLoading()
                bundleListInteractor?.checkProductAvailability(
                    bundleIdentifier,
                    billingClient
                )?.subscribe({
                    context?.runOnUiThread {
                        view.hideLoading()
                        view.buyFromInAppPurchase(it)
                    }
                }, {
                    context?.runOnUiThread {
                        view.hideLoading()
                        showErrorView(it) {}
                    }
                })?.let {
                    compositeDisposable?.add(it)
                }
            } else
                view.showMessageDialog(R.string.error_no_internet_connection)
        }
    }
}