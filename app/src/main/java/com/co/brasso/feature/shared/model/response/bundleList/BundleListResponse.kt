package com.co.brasso.feature.shared.model.response.bundleList

import com.google.gson.annotations.SerializedName

data class BundleListResponse(

	@field:SerializedName("paid_point")
	val paidPoint: Int? = null,

	@field:SerializedName("bundle_list")
	val bundleList: MutableList<BundleListItem>? = null,

	@field:SerializedName("first_expiry")
	val firstExpiry: FirstExpiry? = null,

	@field:SerializedName("free_point")
	val freePoint: Int? = null,

	@field:SerializedName("second_expiry")
	val secondExpiry: SecondExpiry? = null
)