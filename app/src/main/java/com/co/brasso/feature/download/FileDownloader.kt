package com.co.brasso.feature.download

import android.app.DownloadManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.Uri
import android.os.Environment
import android.util.Log
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.co.brasso.database.room.AppDatabase
import com.co.brasso.utils.constant.BundleConstant
import com.co.brasso.utils.constant.StringConstants
import com.co.brasso.utils.extension.getSubscription
import com.co.brasso.utils.util.Logger
import io.reactivex.disposables.CompositeDisposable
import java.io.File

class FileDownloader(var context: Context, var compositeDisposable: CompositeDisposable?) {
    var downloadingList: MutableList<FileDownload> = mutableListOf()
    var broadCastReceiver: MutableList<BroadcastReceiver>? = mutableListOf()

    fun stopDownload(downloadedID: Long) {
        getDownloadManager().remove(downloadedID)
        val download = downloadingList.find { it.downLoadFileId == downloadedID }
        if (download != null) {
            downloadingList.removeAt(downloadingList.indexOf(download))
            removeDownload(download)
        }
    }

    fun downloadFile(
        filePath: String?, fileName: String, buttonType: Int?
    ) {
        try {
            val request = getRequest(filePath, fileName)

            val downloadManager = getDownloadManager()
            val downloadFileId = downloadManager.enqueue(request)

            val fileDownload = FileDownload(
                fileName = fileName,
                filePath = filePath,
                isDownloadComplete = false,
                downLoadFileId = downloadFileId,
                buttonType = buttonType
            )
            downloadingList.add(fileDownload)
            updateDownload(fileDownload)
            registerCompleteReceiver()
        } catch (exception: Exception) {
            Logger.d("jp", exception.toString())
        }
    }

    private fun getDownloadManager() =
        context.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager

    private fun getRequest(
        filePath: String?,
        fileName: String?
    ) = DownloadManager.Request(Uri.parse(filePath))
        .setAllowedNetworkTypes(DownloadManager.Request.NETWORK_WIFI or DownloadManager.Request.NETWORK_MOBILE)
        .setNotificationVisibility(DownloadManager.Request.VISIBILITY_HIDDEN)
        .setDestinationInExternalFilesDir(
            context,
            Environment.DIRECTORY_DOWNLOADS, fileName
        )

    private fun updateDownloadList(downloadedID: Long?) {
        Log.d("jpDownload","updateDownloadList")
        val downloadManager = getDownloadManager()
        val downloadUri = downloadManager.getUriForDownloadedFile(downloadedID ?: -1L)

        val download = downloadingList.find { it.downLoadFileId == downloadedID }
        if (downloadUri != null && download != null) {
            download.isDownloadComplete = true
            updateDownload(download)
            changeFileName(download.fileName)
            updateDownLoadCount()
            downloadingList.removeAt(downloadingList.indexOf(download))
        } else if (download != null) {
            downloadManager.remove(downloadedID ?: 1L)
            downloadingList.removeAt(downloadingList.indexOf(download))
        }
    }

     fun changeFileName(fileName: String?) {
        val file =
            File(context.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS)?.absolutePath +  "/$fileName")

        if (file.exists()) {
            val newFile =
                File(context.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS)?.absolutePath +  "/downloaded-$fileName")
            file.renameTo(newFile)
        }
    }

    private fun updateDownload(fileDownload: FileDownload?) {
        FileDownloadRepository.updateFileDownloadToDb(
            AppDatabase.getAppDatabase(context),
            fileDownload
        ).getSubscription()?.subscribe({
            Log.d("jpDownload","updateDownloadCompleted")
            videoDownloadComplete(fileDownload)
        }, {
            Log.d("jp", it.localizedMessage)
        })?.let {

        }
    }

    private fun updateDownLoadCount() {
        FileDownloadRepository.updateDownloadCount().subscribe({}, {})
    }

    private fun removeDownload(fileDownload: FileDownload?) {
        FileDownloadRepository.deleteFileDownloadDb(
            AppDatabase.getAppDatabase(context),
            fileDownload
        ).getSubscription()?.subscribe()?.let {}
    }

//    override fun onDestroy() {
//        broadCastReceiver?.forEach {
//            try {
//                unregisterReceiver(it)
//            } catch (e: IllegalArgumentException) {
//                Log.d("Service", e.localizedMessage)
//            }
//        }
//        broadCastReceiver = mutableListOf()
//        super.onDestroy()
//    }

    private fun sendBroadcast(intent: Intent) {
        LocalBroadcastManager.getInstance(context).sendBroadcast(intent)
    }

    private fun registerCompleteReceiver() {
        val intent = IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE)
        context.registerReceiver(onComplete, intent)
        broadCastReceiver?.add(onComplete)
    }

    private var onComplete: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(ctxt: Context?, intent: Intent?) {
            val downloadedID = intent?.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1)
            if (downloadedID != -1L)
                updateDownloadList(downloadedID)
            Log.d("jpDownload","DownloadCompleted")
        }
    }

    private fun videoDownloadComplete(fileDownload: FileDownload?) {
        Log.d("jpDownload","videoDownloadCompleteBroadCast")
        sendBroadcast(
            Intent(StringConstants.videoDownloadAction)
                .putExtra(BundleConstant.downloadedFile, fileDownload)
        )
    }
}