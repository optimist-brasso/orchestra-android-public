package com.co.brasso.feature.shared.model.response.purchaseList

import com.google.gson.annotations.SerializedName

data class ConductorItem(

	@field:SerializedName("duration")
	val duration: Int? = null,

	@field:SerializedName("image")
	val image: String? = null,

	@field:SerializedName("title_jp")
	val titleJp: String? = null,

	@field:SerializedName("id")
	val id: Int? = null,

	@field:SerializedName("title")
	val title: String? = null,

	@field:SerializedName("type")
	val type: String? = null,

	@field:SerializedName("record_time")
	val recordTime: String? = null,

	@field:SerializedName("release_date")
	val releaseDate: String? = null,

	@field:SerializedName("tags")
	val tags: List<String?>? = null
)