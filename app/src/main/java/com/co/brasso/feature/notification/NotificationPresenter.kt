package com.co.brasso.feature.notification

import com.co.brasso.R
import com.co.brasso.feature.shared.base.BasePresenter
import com.co.brasso.feature.shared.model.response.appinfo.AppInfoGlobal
import com.co.brasso.feature.shared.model.response.notification.NotificationResponse

class NotificationPresenter : BasePresenter<NotificationView>() {

    private var notificationInteractor: NotificationInteractor? = null

    override fun attachView(view: NotificationView) {
        super.attachView(view)
        notificationInteractor = NotificationInteractor()
    }

    override fun detachView() {
        notificationInteractor = null
        super.detachView()
    }

    fun getNotificationList(isSwipeToRefresh: Boolean) {
        ifViewAttached { view ->
            if (!view.isNetworkAvailable()) {
                view.showMessageDialog(R.string.error_no_internet_connection)
                return@ifViewAttached
            }
            if (isSwipeToRefresh)
                view.showLoading()
            else
                view.showProgressBar()
            getToken().flatMap { token ->
                notificationInteractor?.getNotificationList(compositeDisposable, token)
            }.subscribe({
                if (isSwipeToRefresh)
                    view.hideLoading()
                else
                    view.hideProgressBar()
                AppInfoGlobal.notifications = it as MutableList<NotificationResponse>?
                if (view.getLoginState())
                    view.setNotificationList(it)
                else
                    getDataFormDataBase(it)
            }, {
                if (isSwipeToRefresh)
                    view.hideLoading()
                else
                    view.hideProgressBar()
                showErrorView(it) {}
            }).let {
                compositeDisposable?.add(it)
            }
        }
    }

    private fun getDataFormDataBase(apiResponse: MutableList<NotificationResponse>?) {
        ifViewAttached { view ->
            notificationInteractor?.getAllNotifications(compositeDisposable, view.getAppDatabase())
                ?.subscribe({
                    val newDataBaseList: MutableList<NotificationResponse> = mutableListOf()
                    newDataBaseList.addAll(apiResponse ?: mutableListOf())
                    apiResponse?.forEach { apiData ->
                        val databaseData = it.find { it.id == apiData.id }
                        if (databaseData?.seenStatus == true) {
                            val index = apiResponse.indexOf(apiData)
                            newDataBaseList.removeAt(index)
                            newDataBaseList.add(index, databaseData)
                        }
                    }
                    view.setNotificationList(newDataBaseList)
                }, {

                }).let {
                    if (it != null)
                        compositeDisposable?.add(it)
                }
        }
    }
}