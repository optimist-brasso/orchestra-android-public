package com.co.brasso.feature.shared.model.response.emaillogin

import com.google.gson.annotations.SerializedName

data class EmailLoginResponse(

	@field:SerializedName("access_token")
	val accessToken: String? = null,

	@field:SerializedName("refresh_token")
	val refreshToken: String? = null,

	@field:SerializedName("token_type")
	val tokenType: String? = null,

	@field:SerializedName("expires_in")
	val expiresIn: Int? = null
)