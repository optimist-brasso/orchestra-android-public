package com.co.brasso.feature.shared.model.request

import com.google.gson.annotations.SerializedName

data class FireBaseRegister(
    @SerializedName("fcm_token")
    var fcmToken: String? = null
)