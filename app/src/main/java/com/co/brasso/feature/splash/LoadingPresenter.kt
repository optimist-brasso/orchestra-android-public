package com.co.brasso.feature.splash


import android.app.Activity
import com.android.billingclient.api.BillingClient
import com.android.billingclient.api.Purchase
import com.co.brasso.R
import com.co.brasso.feature.landing.myPage.tab.myPage.profile.MyProfileFragmentInteractor
import com.co.brasso.feature.shared.base.BasePresenter
import com.co.brasso.feature.shared.model.request.BoughtInAppProduct
import com.co.brasso.feature.shared.model.request.BoughtProducts
import com.co.brasso.feature.shared.model.response.appinfo.AppInfoGlobal
import com.co.brasso.feature.shared.model.response.cartList.CartListResponse
import com.co.brasso.feature.shared.model.response.notification.NotificationResponse
import com.co.brasso.utils.extension.getSubscription
import java.io.IOException


class LoadingPresenter : BasePresenter<LoadingView>() {

    private var loadingInteractor: LoadingInteractor? = null
    private var myProfileFragmentInteractor: MyProfileFragmentInteractor? = null

    override fun attachView(view: LoadingView) {
        super.attachView(view)
        loadingInteractor = LoadingInteractor()
        myProfileFragmentInteractor = MyProfileFragmentInteractor()
    }

    override fun detachView() {
        loadingInteractor = null
        myProfileFragmentInteractor = null
        super.detachView()
    }

    fun getAppInfo(billingClient: BillingClient?, activity: Activity?) {
        ifViewAttached { view ->
            view.showProgress()
            getToken().flatMap {
                loadingInteractor?.getAppInfoAndNotifications(
                    compositeDisposable, it, billingClient
                )
            }.subscribe({ info ->
                AppInfoGlobal.appInfo = info.appInfo
                if (view.getLoginState()) {
                    AppInfoGlobal.notifications =
                        info.notifications as? MutableList<NotificationResponse>?
                    if (info.purchaseList?.filter { !it.isAcknowledged && it.purchaseState== Purchase.PurchaseState.PURCHASED }
                            .isNullOrEmpty()) view.openStartingPage()
                    else {
                        val purchases = info.purchaseList?.filter { !it.isAcknowledged  && it.purchaseState== Purchase.PurchaseState.PURCHASED }
                        val listOfProducts: MutableList<BoughtInAppProduct> = mutableListOf()
                        val listOfCarts: MutableList<CartListResponse> = mutableListOf()
                        purchases?.forEach { purchase ->
                            val boughtInAppPurchase = BoughtInAppProduct(
                                purchase.orderId,
                                purchase.packageName,
                                purchase.products[0],
                                purchase.purchaseTime,
                                purchase.purchaseState,
                                purchase.purchaseToken,
                                purchase.quantity,
                                purchase.isAcknowledged
                            )
                            listOfProducts.add(boughtInAppPurchase)
                        }
                        purchaseVerify(
                            billingClient,
                            BoughtProducts(listOfProducts, listOfCarts),
                            purchases,
                            activity
                        )
                    }
                } else saveInDataBase(info.notifications as? MutableList<NotificationResponse>?)
            }, {
                view.hideProgress()
                showErrorView(it) {
                    if (it is IOException) view.recallApi()
                    else view.closeApp()
                }
            }).let {
                compositeDisposable?.add(it)
            }
        }
    }


    private fun saveInDataBase(apiResponse: MutableList<NotificationResponse>?) {
        ifViewAttached { view ->
            loadingInteractor?.getAllNotifications(compositeDisposable, view.getAppDatabase())
                ?.flatMap {
                    val newDataBaseList: MutableList<NotificationResponse> = mutableListOf()
                    apiResponse?.forEach { apiData ->
                        val databaseData = it.find { it.id == apiData.id }
                        if (databaseData != null) {
                            newDataBaseList.add(databaseData)
                        } else {
                            newDataBaseList.add(apiData)
                        }
                    }
                    loadingInteractor?.deleteNotifications(view.getAppDatabase())?.flatMap {
                        loadingInteractor?.saveNotificationsToDb(
                            view.getAppDatabase(), newDataBaseList
                        )?.getSubscription()
                    }?.getSubscription()
                }?.subscribe({
                    view.openStartingPage()
                }, {

                }).let {
                    if (it != null) compositeDisposable?.add(it)
                }
        }
    }


    fun getProfile() {
        ifViewAttached { view ->
            if (view.isNetworkAvailable()) {
                getToken().flatMap { token ->
                    myProfileFragmentInteractor?.getProfile(
                        compositeDisposable, token
                    )
                }.subscribe({
                    view.setProfileData(it)
                }, {
                    showErrorView(it) {

                    }

                }).let {
                    compositeDisposable?.add(it)
                }
            } else view.showMessageDialog(R.string.error_no_internet_connection) { view.closeApp() }
        }
    }

    fun purchaseVerify(
        billingClient: BillingClient?,
        boughtProducts: BoughtProducts?,
        purchases: List<Purchase>?,
        activity: Activity?
    ) {
        ifViewAttached { view ->
            if (view.isNetworkAvailable()) {
                getToken().flatMap { token ->
                    loadingInteractor?.purchaseVerify(compositeDisposable, boughtProducts, token)
                }.subscribe({
                    view.hideLoading()
                    if (purchases?.isEmpty() == false) {
                        var isLastPurchase = false
                        purchases.forEachIndexed { index, purchase ->
                            if (index == (purchases.size - 1)) isLastPurchase = true
                            handlePurchase(activity, billingClient, purchase, isLastPurchase)
                        }
                    } else view.openStartingPage()
                }, {
                    view.hideLoading()
                    showErrorView(it) {}
                }).let {
                    compositeDisposable?.add(it)
                }
            } else view.showMessageDialog(R.string.error_no_internet_connection) { view.closeApp() }
        }
    }

    private fun handlePurchase(
        context: Activity?,
        billingClient: BillingClient?,
        purchase: Purchase,
        isLastPurchase: Boolean
    ) {
        ifViewAttached { view ->
            loadingInteractor?.enableConsumableProduct(purchase, billingClient)?.subscribe({
                context?.runOnUiThread {
                    view.hideLoading()
                    if (isLastPurchase) view.openStartingPage()
                }
            }, {
                context?.runOnUiThread {
                    view.hideLoading()
                    showErrorView(it) {

                    }
                }
            })?.let {
                compositeDisposable?.add(it)
            }
        }
    }
}