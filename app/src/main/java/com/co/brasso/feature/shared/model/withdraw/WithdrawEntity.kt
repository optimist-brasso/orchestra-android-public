package com.co.brasso.feature.shared.model.withdraw

import com.google.gson.annotations.SerializedName

data class WithdrawEntity(
    @SerializedName("remarks")
    var remarks: String? = null
)