package com.co.brasso.feature.landing.myPage.tab.myPage.billingHistory

import com.co.brasso.R
import com.co.brasso.feature.shared.base.BasePresenter

class BillingHistoryPresenter : BasePresenter<BillingHistoryView>() {

    private var billingHistoryInteractor: BillingHistoryInteractor? = null

    override fun attachView(view: BillingHistoryView) {
        super.attachView(view)
        billingHistoryInteractor = BillingHistoryInteractor()
    }

    override fun detachView() {
        billingHistoryInteractor = null
        super.detachView()
    }

    fun getBillingHistory(isPullToRefresh: Boolean) {
        ifViewAttached { view ->
            if (view.isNetworkAvailable()) {
                if (isPullToRefresh)
                    view.showLoading()
                else
                    view.showProgressBar()
                getToken().flatMap { token ->
                    billingHistoryInteractor?.getBillingHistory(
                        compositeDisposable,
                        token,
                    )
                }.subscribe({
                    if (isPullToRefresh)
                        view.hideLoading()
                    else
                        view.hideProgressBar()
                    view.setBillingHistory(it)
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