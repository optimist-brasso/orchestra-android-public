package com.co.brasso.feature.shared.model.registration

import com.google.gson.annotations.SerializedName
import java.io.File

data class UserRegistrationEntity(
    @SerializedName("profile_image")
    var profileImage: File? = null,

    var username: String? = null,

    var name: String? = null,

    var gender: String? = null,

    @SerializedName("music_instrument")
    var musicInstrument: String? = null,

    @SerializedName("professional_id")
    var professionalID: String? = null,

    var dob: String? = null,

    @SerializedName("postal_code")
    var postalCode: String? = null,

    var email: String? = null,

    @SerializedName("short_description")
    var shortDescription: String? = null
)