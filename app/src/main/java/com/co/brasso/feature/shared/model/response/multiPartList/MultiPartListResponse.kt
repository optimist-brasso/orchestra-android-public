package com.co.brasso.feature.shared.model.response.multiPartList

import com.google.gson.annotations.SerializedName

data class MultiPartListResponse(

	@field:SerializedName("MultiPartListResponse")
	val multiPartListResponse: List<MultiPartListResponseItem?>? = null
)
