package com.co.brasso.feature.shared.model.response.hallsound

import com.google.gson.annotations.SerializedName
import java.io.Serializable


data class HallSoundResponse(

    @field:SerializedName("id")
    var id: Int? = null,

    @field:SerializedName("title")
    val title: String? = null,

    @field:SerializedName("title_jp")
    val titleJp: String? = null,

    @field:SerializedName("composer")
    val composer: String? = null,

    @field:SerializedName("conductor")
    val conductor: String? = null,

    @field:SerializedName("introduction")
    val introduction: String? = null,

    @field:SerializedName("tags")
    val tags: List<String>? = null,

    @field:SerializedName("record_time")
    val recordTime: String? = null,

    @field:SerializedName("release_date")
    val releaseDate: String? = null,

    @field:SerializedName("duration")
    var duration: Long? = null,

    @field:SerializedName("image")
    val image: String? = null,

    @field:SerializedName("organization")
    val organization: String? = null,

    @field:SerializedName("organization_diagram")
    val organizationDiagram: String? = null,

    @field:SerializedName("business_type")
    var businessType: String? = null,

    @field:SerializedName("band")
    val band: String? = null,

    @field:SerializedName("jasrac_license_number")
    val jasracLicenseNumber: String? = null,

    @field:SerializedName("venue_title")
    val venueTitle: String? = null,

    @field:SerializedName("venue_description")
    val venueDescription: String? = null,

    @field:SerializedName("is_conductor_favourite")
    var isConductorFavourite: Boolean? = null,

    @field:SerializedName("is_hallsound_favourite")
    var isHallsoundFavourite: Boolean? = null,

    @field:SerializedName("is_session_favourite")
    var isSessionFavourite: Boolean? = null,

    @field:SerializedName("ios_vr_file")
    var iosVrFile: String? = null,

    @SerializedName("is_hallsound_buy")
    var isBought: Boolean? = null,

    @SerializedName("conductor_orchestra_price")
    var conductorOrchestraPrice: Double? = null,

    @SerializedName("hall_sound_price")
    var hallSoundPrice: Double? = null,

    @field:SerializedName("hall_sound")
    var hallSound: MutableList<HallSoundAudio>? = null,

    @SerializedName("musician_id")
    var musicianId: Int? = null,

    var musicTag: Int? = null,

    var type: String? = null,

    @SerializedName("is_premium")
    var isPremium: Boolean? = false,

    var sessionId: Int? = null,

    var isFromPremiumPage: Boolean? = false,

    var isFromVideoPlayer: Boolean? = false,

    var downLoadFileId: Long? = null,

    var vrFilePath: String? = null,

    var vrFileName: String? = null,

    var sessionType: String? = null,

    var seatPosition: String? = null,

    @SerializedName("hall_sound_identifier")
    var hallSoundIdentifier: String? = null,

    @field:SerializedName("vr_thumbnail")
    val vrThumbnail: String? = null,

    var fromPage: String? = null,

    var buttonType: Int? = null,
    @SerializedName("left_view_angle")
    var leftViewAngle: Int? = null,
    @SerializedName("right_view_angle")
    var rightViewAngle: Int? = null,
    @SerializedName("is_bought")
    var isBoughtForUnity: Boolean? = null,
    @SerializedName("play_duration")
    var playDuration: Int? = null,
    @SerializedName("venue_diagram")
    var venueDiagram: String? = null,
    @SerializedName("conductor_image")
    var conductorImage: String? = null,
    @SerializedName("session_image")
    var sessionImage: String? = null,
    @field:SerializedName("vr_file")
    var vrFile: String? = null,
    var instrumentName:String?=null
) : Serializable
