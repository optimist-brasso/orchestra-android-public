package com.co.brasso.feature.shared.model.response.myprofile

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class MyProfileResponse(

    @SerializedName("id")
    val id: Int? = null,

    @SerializedName("name")
    val fullName: String? = null,

    @SerializedName("short_description")
    val shortDescription: String? = null,

    @SerializedName("email")
    val email: String? = null,

    @SerializedName("username")
    val username: String? = null,

    @SerializedName("status")
    val status: String? = null,

    @SerializedName("profile_image")
    var profileImage: String? = null,

    @SerializedName("address")
    val address: String? = null,

    @SerializedName("postal_code")
    val postalCode: String? = null,

    @SerializedName("dob")
    val dob: String? = null,

    @SerializedName("joined_date")
    val joinedDate: String? = null,

    @SerializedName("gender")
    val gender: String? = null,

    @SerializedName("music_instrument")
    val musicalInstrument: String? = null,

    @SerializedName("registration_date")
    val registrationDate: String? = null,

    @SerializedName("professional")
    val professional: String? = null,

    @SerializedName("professional_id")
    val professionalId: Int? = null,

    @SerializedName("profile_status")
    val profileStatus: Boolean? = null,

    @SerializedName("total_points")
    var totalPoints: Int? = null,
    @SerializedName("notification_status")
    var notificationStatus: Boolean? = false

) : Serializable