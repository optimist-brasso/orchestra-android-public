package com.co.brasso.feature.notification.notificationDetails

import com.co.brasso.R
import com.co.brasso.feature.shared.base.BasePresenter
import com.co.brasso.feature.shared.model.response.notification.NotificationResponse
import com.co.brasso.utils.extension.getSubscription

class NotificationDetailPresenter : BasePresenter<NotificationDetailView>() {

    private var notificationDetailInteractor: NotificationDetailInteractor? = null

    var emitter:Int?=0

    override fun attachView(view: NotificationDetailView) {
        super.attachView(view)
        notificationDetailInteractor = NotificationDetailInteractor()
    }

    override fun detachView() {
        notificationDetailInteractor = null
        super.detachView()
    }

    fun getNotificationDetail(notificationId: String?) {
        ifViewAttached { view ->
            if (!view.isNetworkAvailable()) {
                view.showMessageDialog(R.string.error_no_internet_connection)
                view.hideDetail()
                return@ifViewAttached
            }
            view.showProgressBar()
            getToken().flatMap { token ->
                notificationDetailInteractor?.getNotificationDetail(
                    compositeDisposable,
                    token,
                    notificationId
                )
            }.subscribe({
                view.showDetail()
                view.hideProgressBar()
                view.setNotificationDetail(it)
            }, {
                view.hideProgressBar()
                showErrorView(it) {
                    view.popUpBackStack()
                }
            }).let {
                compositeDisposable?.add(it)
            }
        }
    }


    fun updateNotificationSeenStatus(notification: NotificationResponse?) {
        ifViewAttached { view ->
            notificationDetailInteractor?.updateNotificationsToDb(
                view.getAppDatabase(),
                notification
            ).getSubscription()?.subscribe({}, {})?.let {
                compositeDisposable?.add(it)
            }
        }
    }


}