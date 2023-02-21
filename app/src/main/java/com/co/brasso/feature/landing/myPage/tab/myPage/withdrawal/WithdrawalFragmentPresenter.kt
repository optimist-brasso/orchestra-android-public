package com.co.brasso.feature.landing.myPage.tab.myPage.withdrawal

import com.co.brasso.R
import com.co.brasso.feature.shared.base.BasePresenter
import com.co.brasso.feature.shared.model.withdraw.WithdrawEntity

class WithdrawalFragmentPresenter : BasePresenter<WithdrawalFragmentView>() {
    private var withdrawalFragmentInteractor: WithdrawalFragmentInteractor? = null

    override fun attachView(view: WithdrawalFragmentView) {
        super.attachView(view)
        withdrawalFragmentInteractor = WithdrawalFragmentInteractor()
    }

    override fun detachView() {
        withdrawalFragmentInteractor = null
        super.detachView()
    }

    fun proceedToWithdraw(withdrawEntity: WithdrawEntity) {
        ifViewAttached { view ->
            if (view.isNetworkAvailable()) {
                view.showProgressBar()
                getToken().flatMap { token ->
                    withdrawalFragmentInteractor?.proceedToWithdraw(compositeDisposable, token , withdrawEntity)
                }.subscribe({
                    view.hideProgressBar()
                    view.withdrawSuccess(it)
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
}