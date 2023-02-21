package com.co.brasso.feature.shared.model.response.hallsound

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class HallSoundAudio(
    var position: Int? = null,
    var type: String? = null,
    var image: String? = null,
    @SerializedName("audio_file")
    var audioFile: String? = null,
    var fileName: String? = null,
    var filePath: String? = null,
    var isDownloaded:Boolean?=false
):Serializable
