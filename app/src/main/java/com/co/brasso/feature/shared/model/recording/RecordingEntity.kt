package com.co.brasso.feature.shared.model.recording

import com.google.gson.annotations.SerializedName
import java.io.File

data class RecordingEntity(
    @SerializedName("orchestra_id")
    var orchestraId: String? = null,
    var title: String? = null,
    @SerializedName("recording_date")
    var recordingDate: String? = null,
    var duration: String? = null,
    @SerializedName("file")
    var recordingFile: File? = null,
    var thumbnail: File? = null
)