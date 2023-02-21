package com.co.brasso.feature.shared.model.response.appinfo

import com.co.brasso.feature.shared.model.response.cartList.CartListResponse
import com.co.brasso.feature.shared.model.response.myprofile.MyProfileResponse
import com.co.brasso.feature.shared.model.response.notification.NotificationResponse

object AppInfoGlobal {
    var appInfo: AppInfo? = null
    var cartInfo: MutableList<CartListResponse>? = null
    var notifications:MutableList<NotificationResponse>?=null
    var myProfileResponse: MyProfileResponse?=null
}