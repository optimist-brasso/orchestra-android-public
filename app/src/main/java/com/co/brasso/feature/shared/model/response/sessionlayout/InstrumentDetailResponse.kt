package com.co.brasso.feature.shared.model.response.sessionlayout

import com.co.brasso.feature.shared.model.response.hallsound.HallSoundResponse
import com.co.brasso.feature.shared.model.response.playerdetail.PlayerDetailResponse
import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class InstrumentDetailResponse(

    @field:SerializedName("orchestra")
    val hallSoundDetail: HallSoundResponse? = null,

    @field:SerializedName("player")
    val playerDetail: PlayerDetailResponse? = null,

    @field:SerializedName("name")
    val name: String? = null,

    @field:SerializedName("description")
    val description: String? = null,

    @field:SerializedName("id")
    val id: Int? = null,

    @field:SerializedName("is_favourite")
    var isFavourite: Boolean? = null,

    @field:SerializedName("offer_title")
    val offerTitle: Any? = null,

    @field:SerializedName("musician")
    val musician: String? = null,

    @field:SerializedName("vr_file")
    var vrFile: String? = null,

    @field:SerializedName("ios_vr_file")
    var iosVrFile: String? = null,

    @field:SerializedName("premimum_file")
    var premiumFile: String? = null,

    @field:SerializedName("part_price")
    val partPrice: Double? = null,

    @field:SerializedName("premium_video_price")
    val premiumVideoPrice: Double? = null,

    @field:SerializedName("premium_contents")
    val premiumContents: String? = null,

    @field:SerializedName("premium_video_description")
    val premiumVideoDescription: String? = null,

    @field:SerializedName("vr_thumbnail")
    val vrThumbnail: String? = null,

    @field:SerializedName("premium_thumbnail")
    val premiumThumbnail: String? = null,

    @SerializedName("part_identifier")
    val partIdentifier: String? = null,

    @SerializedName("premium_video_identifier")
    val premiumVideoIdentifier: String? = null,

    @SerializedName("android_premium_vr_file")
    val androidPremiumVrFile: String? = null,

    @SerializedName("ios_premium_vr_file")
    val iosPremiumVrFile: String? = null,

    @SerializedName("combo_identifier")
    val comboVideoIdentifier: String? = null,

    @field:SerializedName("combo_price")
    val comboPrice: Double? = null,

    @SerializedName("is_part_bought")
    var isPartBought: Boolean? = false,

    @SerializedName("is_premium_bought")
    var isPremiumBought: Boolean? = false,

    @SerializedName("part_status")
    var partStatus: Boolean? = false,

    @SerializedName("part_payment")
    var partPayment: Boolean? = false,

    @SerializedName("premium_status")
    var premiumStatus: Boolean? = false,

    @SerializedName("premium_payment")
    var premiumPayment: Boolean? = false,

    @SerializedName("combo_status")
    var comboStatus: Boolean? = false,

    @SerializedName("combo_payment")
    var comboPayment: Boolean? = false,

    var type: String? = null,

    var playerName: String? = null,

    @SerializedName("instrument_id")
    var instrumentId: Int? = null,

    var isFormSession: Boolean? = false,

    var isFromPremiumPage: Boolean? = false,

    var isFromAppendixVideo: Boolean? = false,

    var isDownloaded: Boolean? = false,
    @SerializedName("left_view_angle")
    var leftViewAngle: Int? = null,
    @SerializedName("right_view_angle")
    var rightViewAngle: Int? = null,
    @SerializedName("is_premium")
    var isPremium:Boolean?=false,
    @SerializedName("premium_vr_thumbnail")
    val  premiumVrThumbnail:String?=null,
) : Serializable