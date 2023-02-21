package com.co.brasso.feature.shared.model.response

import java.io.Serializable

data class NotificationItems(
    var title: String? = null,
    var description: String? = null,
    var notificationDate: String? = null,
    var imageLink: String? = null,
    var color: Int? = null
) : Serializable