package com.co.brasso.feature.notification

import com.co.brasso.feature.shared.base.BaseView
import com.co.brasso.feature.shared.model.response.notification.NotificationResponse

interface NotificationView : BaseView {
    fun showProgressBar()

    fun hideProgressBar()

    fun setNotificationList(notificationResponse: List<NotificationResponse>)
}