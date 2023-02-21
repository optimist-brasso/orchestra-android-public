package com.co.brasso.feature.shared.model.response.bundleList

import com.google.gson.annotations.SerializedName

data class FirstExpiry(

	@field:SerializedName("date")
	val date: String? = null,

	@field:SerializedName("point")
	val point: Int? = null
)