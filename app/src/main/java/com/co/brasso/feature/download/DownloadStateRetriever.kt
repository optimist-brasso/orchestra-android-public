package com.co.brasso.feature.download

import android.app.DownloadManager
import android.content.Context
import android.database.Cursor
import com.co.brasso.feature.shared.base.BaseRepository
import io.reactivex.Observable
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import java.util.concurrent.TimeUnit
import java.util.concurrent.atomic.AtomicBoolean

class DownloadStateRetriever(val context: Context?) {
    private var downloadManager: DownloadManager =
        context?.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager

    fun getProgress(id: Long, compositeDisposable: CompositeDisposable?) =
        Observable.create<DownloadProgress> { e ->
            val downloading = AtomicBoolean(true)

            Observable.fromCallable {
                val query = DownloadManager.Query().setFilterById(id)
                val cursor = downloadManager.query(query)

                cursor.moveToFirst()

                val bytesDownloaded = getDownloaded(cursor)
                val bytesTotal = getTotal(cursor)

                if (isSuccessful(cursor) || isFailed(cursor)) downloading.set(false)
                cursor.close()

                DownloadProgress(downloadId = id, progress = bytesDownloaded, fileSize = bytesTotal)
            }
                .subscribeOn(Schedulers.newThread())
                .delay(500, TimeUnit.MILLISECONDS)
                .repeatUntil { !downloading.get() }
                .subscribe({
                    if (!downloading.get()) {
                        if (it.progress == it.fileSize) {
                            e.onNext(it)
                            e.onComplete()
                        } else{
                            if(isStorageFull(id)){
                                e.onError(BaseRepository.DiskFullError("ストレージが一杯です"))
                            }else{
                                e.onError(Throwable("Video download was cancelled"))
                            }
                        }
                    } else {
                        if (it.fileSize > 0)
                            e.onNext(it)
                    }
                }, {
                    e.onError(it)
                })?.let { compositeDisposable?.add(it) }
        }

    private fun getTotal(cursor: Cursor) = cursor.intValue(DownloadManager.COLUMN_TOTAL_SIZE_BYTES)


    private fun isStorageFull(id: Long):Boolean{
        val query = DownloadManager.Query().setFilterById(id)
        val cursor = downloadManager.query(query)

        if(cursor.moveToFirst()){
            val status = cursor.intValue(DownloadManager.COLUMN_STATUS)
            val reason = cursor.intValue(DownloadManager.COLUMN_REASON)
            if (status == DownloadManager.STATUS_FAILED && reason == DownloadManager.ERROR_INSUFFICIENT_SPACE) {
                cursor.close()
                return true
            }
        }
        cursor.close()
        return false
    }

    private fun getDownloaded(cursor: Cursor) =
        cursor.intValue(DownloadManager.COLUMN_BYTES_DOWNLOADED_SO_FAR)

    private fun isSuccessful(cursor: Cursor) = status(cursor) == DownloadManager.STATUS_SUCCESSFUL

    private fun isFailed(cursor: Cursor) = status(cursor) == DownloadManager.STATUS_FAILED

    private fun status(cursor: Cursor) = cursor.intValue(DownloadManager.COLUMN_STATUS)

    fun isInDownloadList(id: Long) = Observable.fromCallable {
        val query = DownloadManager.Query().setFilterById(id)
        query?.setFilterByStatus(DownloadManager.STATUS_RUNNING)
        val cursor = downloadManager.query(query)

        cursor.moveToFirst()
    }

    fun isInDownloadList(list: MutableList<FileDownload?>) = Observable.fromCallable {
        var isNext = false

        for (file in list) {
            file?.downLoadFileId?.let {
                val query = DownloadManager.Query().setFilterById(it)

                query?.setFilterByStatus(DownloadManager.STATUS_RUNNING)
                val cursor = downloadManager.query(query)

                isNext = cursor.moveToFirst()
            }

            if (isNext)
                break
        }

        isNext
    }

    fun isDownloaded(id: Long) = downloadManager.getUriForDownloadedFile(id) != null
}
