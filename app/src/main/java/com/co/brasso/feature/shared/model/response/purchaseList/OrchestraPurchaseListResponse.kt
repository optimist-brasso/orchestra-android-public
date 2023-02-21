package com.co.brasso.feature.shared.model.response.purchaseList

import com.google.gson.annotations.SerializedName

data class OrchestraPurchaseListResponse(

	@field:SerializedName("hall_sound")
	val hallSound: List<HallSoundItem>? = null,

	@field:SerializedName("premium")
	val premium: List<PremiumItem>? = null,

	@field:SerializedName("part")
	val part: List<PartItem>? = null,

	@field:SerializedName("conductor")
	val conductor: List<ConductorItem>? = null
)