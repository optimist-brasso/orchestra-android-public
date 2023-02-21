package com.co.brasso.feature.landing.myPage.tab.myPage

import com.co.brasso.R
import com.co.brasso.feature.shared.base.BasePresenter

class MyPageMenuFragmentPresenter : BasePresenter<MyPageMenuFragmentView>() {
    private var myPageMenuFragmentInteractor: MyPageMenuFragmentInteractor? = null

    override fun attachView(view: MyPageMenuFragmentView) {
        super.attachView(view)
        myPageMenuFragmentInteractor = MyPageMenuFragmentInteractor()
    }

    override fun detachView() {
        myPageMenuFragmentInteractor = null
        super.detachView()
    }

    fun unRegisterFcm() {
        ifViewAttached { view ->
            if (!view.isNetworkAvailable()) {
                view.showMessageDialog(R.string.error_no_internet_connection)
                return@ifViewAttached
            }
            view.showLoading()
            getToken().flatMap {
                myPageMenuFragmentInteractor?.unRegisterFcm(it, compositeDisposable)
            }.subscribe({
                view.hideLoading()
                view.logoutSuccessfully()
            }, {
                view.hideLoading()
                showErrorView(it) {}
            }).let {
                compositeDisposable?.add(it)
            }
        }
    }
}