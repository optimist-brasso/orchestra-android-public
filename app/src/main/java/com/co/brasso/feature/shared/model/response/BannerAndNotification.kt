package com.co.brasso.feature.shared.model.response

import com.co.brasso.feature.shared.model.response.home.HomeData
import com.co.brasso.feature.shared.model.response.notification.NotificationResponse

data class BannerAndNotification(
    var homeData: HomeData? = null,
    var notificationResponse: List<NotificationResponse>? = null
)