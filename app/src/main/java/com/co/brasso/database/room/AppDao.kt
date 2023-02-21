package com.co.brasso.database.room

import androidx.room.*
import com.co.brasso.feature.download.FileDownload
import com.co.brasso.feature.shared.model.response.notification.NotificationResponse
import com.co.brasso.utils.constant.DbConstants
import io.reactivex.Single

@Dao
interface AppDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertNotifications(notifications: List<NotificationResponse>?)

    @Query("SELECT * FROM ${DbConstants.tableNotifications}")
    fun getAllNotifications(): Single<List<NotificationResponse>?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertNotification(notification: NotificationResponse?)

    @Transaction
    fun insertOrUpdateFileDownload(fileDownload: FileDownload?) {
        val id = insertFileDownload(fileDownload)
        if (id == -1L) {
            updateFileDownload(fileDownload)
        }
    }

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertFileDownload(fileDownload: FileDownload?): Long

    @Update(onConflict = OnConflictStrategy.REPLACE)
    fun updateFileDownload(fileDownload: FileDownload?)

    @Query("SELECT * FROM ${DbConstants.fileDownload} WHERE fileName = :fileName LIMIT :limit OFFSET :offset")
    fun getFileDownload(
        fileName: String?,
        limit: Int? = 1,
        offset: Int? = 0
    ): Single<List<FileDownload>>

    @Query("SELECT * FROM ${DbConstants.fileDownload}")
    fun getFileDownloads(): Single<List<FileDownload>>

    @Query("SELECT * FROM ${DbConstants.fileDownload} WHERE isDownloadComplete = :downloadStatus")
    fun getCompletedFileDownloads(downloadStatus:Boolean?=true): Single<List<FileDownload>>

    @Delete
    fun deleteFileDownload(
        fileDownload: FileDownload?
    )

    @Query("DELETE FROM ${DbConstants.tableNotifications}")
    fun deleteNotification()
}