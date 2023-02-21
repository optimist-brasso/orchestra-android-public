package com.co.brasso.feature.download

import com.co.brasso.database.room.AppDatabase
import io.reactivex.disposables.CompositeDisposable

class DownloadInteractor {
    fun getFileDownload(
        compositeDisposable: CompositeDisposable?,
        appDatabase: AppDatabase?,
        fileName: String?
    ) = FileDownloadRepository.getFileDownload(
        compositeDisposable,
        appDatabase,
        fileName
    )

    fun updateDownloadCount(

    ) = FileDownloadRepository.updateDownloadCount(

    )

    fun updateFileDownload(
        fileDownload: FileDownload?,
        appDatabase: AppDatabase?
    ) =
        FileDownloadRepository.updateFileDownloadToDb(
            appDatabase,
            fileDownload
        )

    fun deleteFileDownload(
        fileDownload: FileDownload?,
        appDatabase: AppDatabase?
    ) =
        FileDownloadRepository.deleteFileDownloadDb(
            appDatabase,
            fileDownload
        )
}