package com.co.brasso.feature.shared.model.response.appInfoAndNotification

import com.android.billingclient.api.Purchase
import com.co.brasso.feature.shared.model.response.appinfo.AppInfo
import com.co.brasso.feature.shared.model.response.notification.NotificationResponse

data class AppInfoAndNotification(
    var appInfo: AppInfo,
    var notifications: List<NotificationResponse>? = null,
    var purchaseList: MutableList<Purchase>? = null,
    var successMessage:String?=null
)