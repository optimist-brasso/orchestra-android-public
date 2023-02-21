package com.co.brasso.feature.shared.model.response

import com.android.billingclient.api.ProductDetails
import com.co.brasso.feature.shared.model.response.resendotp.SuccessResponse

data class BuyProducts(
    var success: SuccessResponse? = null,
    var products: MutableList<ProductDetails>? = null
)