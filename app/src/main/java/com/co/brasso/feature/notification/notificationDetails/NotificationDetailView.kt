package com.co.brasso.feature.notification.notificationDetails

import com.co.brasso.feature.shared.base.BaseView
import com.co.brasso.feature.shared.model.response.notification.NotificationResponse

interface NotificationDetailView : BaseView {
    fun setNotificationDetail(notificationResponse: NotificationResponse?)
    fun showProgressBar()
    fun hideProgressBar()
    fun showErrorLayout()
    fun hideErrorLayout()
    fun popUpBackStack()

    fun showDetail()

    fun hideDetail()
}