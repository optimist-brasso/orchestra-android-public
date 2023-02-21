package com.co.brasso.feature.shared.model.updateProfile

import com.google.gson.annotations.SerializedName

data class UpdateProfileEntity(
    var username: String? = null,
    var gender: String? = null,
    var dob: String? = null,
    @SerializedName("music_instrument")
    var musicalInstrument: String? = null,
    @SerializedName("short_description")
    var shortDescription: String? = null,
    @SerializedName("professional_id")
    var professionalID: String? = null,
    @SerializedName("postal_code")
    var postalCode: String? = null,
    @SerializedName("name")
    var name: String? = null
)