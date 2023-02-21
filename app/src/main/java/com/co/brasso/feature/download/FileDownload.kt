package com.co.brasso.feature.download

import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey
import com.co.brasso.utils.constant.DbConstants
import java.io.Serializable

@Entity(tableName = DbConstants.fileDownload)
data class FileDownload(
    @PrimaryKey
    var fileName: String,
    var filePath: String?,
    var isDownloadComplete: Boolean?,
    var downLoadFileId: Long? = null,
    var fileSize: Int = 0,
    var buttonType: Int? = null
) : Serializable {
    constructor(
        fileName: String,
        filePath: String,
        isDownloadComplete: Boolean?,
        downLoadFileId: Long?,
        fileSize: Int
    ) : this(fileName, filePath, isDownloadComplete, downLoadFileId, fileSize, null)
}