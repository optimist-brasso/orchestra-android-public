package com.co.brasso.feature.landing

import com.co.brasso.feature.shared.base.BaseView
import com.co.brasso.feature.shared.model.response.notification.NotificationResponse

interface LandingView : BaseView {
    fun showCartCount()
    fun setNotificationCount(notifications: MutableList<NotificationResponse>?)
}