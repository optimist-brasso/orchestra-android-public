package com.co.brasso.feature.shared.model.response.bundleList

import com.google.gson.annotations.SerializedName

data class BundleListItem(

	@field:SerializedName("identifier")
	val identifier: String? = null,

	@field:SerializedName("price")
	val price: Int? = null,

	@field:SerializedName("paid_point")
	val paidPoint: Int? = null,

	@field:SerializedName("id")
	val id: Int? = null,

	@field:SerializedName("free_point")
	val freePoint: Int? = null,

	@field:SerializedName("status")
	val status: String? = null
)