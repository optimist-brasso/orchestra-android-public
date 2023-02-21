package com.co.brasso.feature.download

import com.co.brasso.database.room.AppDatabase
import com.co.brasso.feature.shared.base.BaseRepository
import com.co.brasso.feature.shared.model.response.hallsound.HallSoundResponse
import com.co.brasso.feature.shared.model.response.resendotp.SuccessResponse
import com.co.brasso.feature.shared.repositories.HomeRepository
import com.co.brasso.utils.extension.getSubscription
import io.reactivex.Completable
import io.reactivex.Single
import io.reactivex.disposables.CompositeDisposable

object FileDownloadRepository : BaseRepository() {

    fun updateDownloadCount(): Single<SuccessResponse> = Single.create { e ->
        apiService?.updateDownloadCount().getSubscription()?.subscribe({
                if (it.isSuccessful && it.body()?.data != null) {
                    e.onSuccess(it.body()?.data ?: SuccessResponse())
                } else {
                    e.onError(getError(it.code(), it.errorBody()?.string()))
                }
            }, {
                e.onError(getDefaultError(it))
            })
    }

    fun updateFileDownloadToDb(
        appDatabase: AppDatabase?, fileDownload: FileDownload?
    ): Single<Boolean> =
        Completable.fromAction { appDatabase?.appDao()?.insertOrUpdateFileDownload(fileDownload) }
            .toSingleDefault(true)

    fun deleteFileDownloadDb(
        appDatabase: AppDatabase?, fileDownload: FileDownload?
    ): Single<FileDownload> = Completable.fromAction {
        appDatabase?.appDao()?.deleteFileDownload(fileDownload)
        fileDownload
    }.toSingleDefault(FileDownload(fileDownload?.fileName ?: "", fileDownload?.filePath, false,null,0,fileDownload?.buttonType))


    fun getFileDownload(
        compositeDisposable: CompositeDisposable?, appDatabase: AppDatabase?, fileName: String?
    ): Single<List<FileDownload>> = Single.create { e ->
        appDatabase?.appDao()?.getFileDownload(fileName)?.getSubscription()?.subscribe({
                if (it != null) e.onSuccess(it)
                else {
                    closeDb(appDatabase)
                    e.onError(getDefaultError())
                }
            }, {
                closeDb(appDatabase)
                e.onError(it)
            })?.let {
                compositeDisposable?.add(it)
            }
    }

    fun getCompletedFileDownloads(
        compositeDisposable: CompositeDisposable?, appDatabase: AppDatabase?
    ): Single<List<FileDownload>> = Single.create { e ->
        appDatabase?.appDao()?.getCompletedFileDownloads()?.getSubscription()?.subscribe({
                if (it != null) e.onSuccess(it)
                else {
                    closeDb(appDatabase)
                    e.onError(getDefaultError())
                }
            }, {
                closeDb(appDatabase)
                e.onError(it)
            })?.let {
                compositeDisposable?.add(it)
            }
    }
}