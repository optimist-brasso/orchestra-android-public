package com.co.brasso.feature.shared.model.response.recordingResponse

import com.google.gson.annotations.SerializedName

data class RecordingItem(

    val id: Int? = null,

    @SerializedName("song_title")
    val songTitle: String? = null,

    @SerializedName("thumbnail")
    val image: String? = null,

    val edition: String? = null,

    @SerializedName("recorded_date")
    val recordedDate: String? = null,

    val duration: String? = null,

    @SerializedName("music_path")
    val musicPath: String? = null
)