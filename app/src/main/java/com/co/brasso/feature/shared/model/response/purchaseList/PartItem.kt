package com.co.brasso.feature.shared.model.response.purchaseList

import com.google.gson.annotations.SerializedName

data class PartItem(

	@field:SerializedName("duration")
	val duration: Int? = null,

	@field:SerializedName("image")
	val image: String? = null,

	@field:SerializedName("musician_id")
	val musicianId: Int? = null,

	@field:SerializedName("instrument_title")
	val instrumentTitle: String? = null,

	@field:SerializedName("title_jp")
	val titleJp: String? = null,

	@field:SerializedName("id")
	val id: Int? = null,

	@field:SerializedName("session_type")
	val sessionType: String? = null,

	@field:SerializedName("title")
	val title: String? = null,

	@field:SerializedName("type")
	val type: String? = null,

	@field:SerializedName("instrument_id")
	val instrumentId: Int? = null,

	@field:SerializedName("record_time")
	val recordTime: String? = null,

	@field:SerializedName("release_date")
	val releaseDate: String? = null,

	@field:SerializedName("tags")
	val tags: List<String?>? = null
)