package com.co.brasso.feature.shared.model.request.auth

data class OtpVerification(
    var token: String? = null,
    var email: String? = null
)