package com.co.brasso.feature.landing.myPage.tab.setting

import com.co.brasso.R
import com.co.brasso.feature.shared.base.BasePresenter
import com.co.brasso.feature.shared.model.response.appinfo.AppInfoGlobal

class MyPageSettingFragmentPresenter : BasePresenter<MyPageSettingFragmentView>() {

    private var myPageSettingFragmentInteractor: MyPageSettingFragmentInteractor? = null

    override fun attachView(view: MyPageSettingFragmentView) {
        super.attachView(view)
        myPageSettingFragmentInteractor = MyPageSettingFragmentInteractor()
    }

    override fun detachView() {
        myPageSettingFragmentInteractor = null
        super.detachView()
    }

    fun setNotificationStatus() {
        ifViewAttached { view ->
            if (!view.isNetworkAvailable()) {
                view.showMessageDialog(R.string.error_no_internet_connection)
                view.changeSwitchState()
                return@ifViewAttached
            }
            getToken().flatMap { token ->
                myPageSettingFragmentInteractor?.setNotificationStatus(
                    compositeDisposable, token
                )
            }.subscribe({
                AppInfoGlobal.myProfileResponse?.notificationStatus =
                    !(AppInfoGlobal.myProfileResponse?.notificationStatus ?: false)
            }, {

            }).let {
                compositeDisposable?.add(it)
            }
        }

    }
}

