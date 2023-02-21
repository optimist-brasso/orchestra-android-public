package com.co.brasso.feature.landing.myPage.tab.myPage.profile

import com.co.brasso.R
import com.co.brasso.feature.shared.base.BasePresenter

class MyProfileFragmentPresenter : BasePresenter<MyProfileFragmentView>() {

    private var myProfileFragmentInteractor: MyProfileFragmentInteractor? = null

    override fun attachView(view: MyProfileFragmentView) {
        super.attachView(view)
        myProfileFragmentInteractor = MyProfileFragmentInteractor()
    }

    override fun detachView() {
        myProfileFragmentInteractor = null
        super.detachView()
    }

    fun getProfile() {
        ifViewAttached { view ->
            if (!view.isNetworkAvailable()) {
                view.hideMainView()
                view.showMessageDialog(R.string.error_no_internet_connection)
                return@ifViewAttached
            }
            getToken().flatMap { token ->
                myProfileFragmentInteractor?.getProfile(
                    compositeDisposable, token
                )
            }.subscribe({
                view.success(it)
            }, {
                view.hideProgressbar()
                showErrorView(it) {}
            }).let {
                compositeDisposable?.add(it)
            }
        }
    }
}