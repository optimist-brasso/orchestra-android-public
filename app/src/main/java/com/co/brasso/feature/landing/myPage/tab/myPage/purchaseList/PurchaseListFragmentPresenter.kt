package com.co.brasso.feature.landing.myPage.tab.myPage.purchaseList

import com.co.brasso.R
import com.co.brasso.feature.shared.base.BasePresenter

class PurchaseListFragmentPresenter : BasePresenter<PurchaseListFragmentView>() {
    private var purchaseListFragmentInteractor: PurchaseListFragmentInteractor? = null

    override fun attachView(view: PurchaseListFragmentView) {
        super.attachView(view)
        purchaseListFragmentInteractor = PurchaseListFragmentInteractor()
    }

    override fun detachView() {
        purchaseListFragmentInteractor = null
        super.detachView()
    }

    fun getPurchaseList(isPullToRefresh: Boolean) {
        ifViewAttached { view ->
            if (view.isNetworkAvailable()) {
                if (isPullToRefresh)
                    view.showLoading()
                else
                    view.showProgressBar()
                getToken().flatMap {
                    purchaseListFragmentInteractor?.getPurchaseList(compositeDisposable, it)
                }.subscribe({
                    if (isPullToRefresh)
                        view.hideLoading()
                    else
                        view.hideProgressBar()
                    view.setRecordingList(it)
                }, {
                    if (isPullToRefresh)
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
}