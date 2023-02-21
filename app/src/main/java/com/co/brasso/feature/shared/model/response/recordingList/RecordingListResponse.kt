package com.co.brasso.feature.shared.model.response.recordingList

import com.co.brasso.utils.constant.Constants
import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class RecordingListResponse(
    val id: Int? = null,

    @SerializedName("song_title")
    val songTitle: String? = null,

    @SerializedName("song_title_jp")
    val songTitleJp: String? = null,

    @SerializedName("thumbnail")
    val image: String? = null,

    val edition: String? = null,

    @SerializedName("recorded_date")
    val recordedDate: String? = null,

    val duration: Long? = null,

    @SerializedName("music_path")
    val musicPath: String? = null,

    var hasNext: Boolean? = false,

    var viewType:String?= Constants.playerListView

) : Serializable