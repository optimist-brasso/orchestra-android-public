package com.co.brasso.feature.download

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Environment
import android.util.Log
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.co.brasso.R
import com.co.brasso.feature.shared.base.BasePresenter
import com.co.brasso.feature.shared.base.BaseRepository
import com.co.brasso.feature.shared.model.response.hallsound.HallSoundAudio
import com.co.brasso.feature.shared.model.response.hallsound.HallSoundResponse
import com.co.brasso.utils.constant.BundleConstant
import com.co.brasso.utils.constant.Constants
import com.co.brasso.utils.constant.StringConstants
import com.co.brasso.utils.extension.getSubscription
import com.co.brasso.utils.util.DialogUtils
import com.co.brasso.utils.util.NetworkUtils
import com.co.brasso.utils.util.PreferenceUtils
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import java.io.File

abstract class DownloadPresenter<V : DownloadView> : BasePresenter<V>() {
    private lateinit var downloadInteractor: DownloadInteractor

    private lateinit var fileDownloader: FileDownloader
    private lateinit var downloadStateRetriever: DownloadStateRetriever
    var orchestra: HallSoundResponse? = null
    private var fileDownloadList = mutableListOf<FileDownload?>()
    private var progressList = mutableListOf<DownloadProgress>()

    override fun attachView(view: V) {
        super.attachView(view)
        downloadInteractor = DownloadInteractor()
    }

    fun initVideoDownloader(context: Context) {
        fileDownloader = FileDownloader(context, compositeDisposable)
        downloadStateRetriever = DownloadStateRetriever(context)
    }

    fun initBoardCastListener(context: Context) {
        LocalBroadcastManager.getInstance(context).registerReceiver(
            downloadCompleteBroadCast, IntentFilter(StringConstants.videoDownloadAction)
        )
    }

    fun refreshData() {
        setOrchestraData(orchestra, orchestra?.vrFile)
        getDownloadedFilePath()
    }

    private fun resetProgress() {
        this.fileDownloadList.clear()
        progressList.clear()
        ifViewAttached {
            it.setProgress(0)
        }
    }

    fun setOrchestraData(orchestra: HallSoundResponse?, vrFile: String? = null) {
        this.orchestra = orchestra
        resetProgress()
        if (vrFile == null || vrFile.isEmpty()) {
            ifViewAttached {
                it.hidePlayButton()
                it.hideDownloadButton()
            }
        } else {
            ifViewAttached {
                it.showPlayButton()
                //    it.showDownloadButton()
            }
            this.orchestra?.vrFile = vrFile
            this.orchestra?.vrFileName = getFileName(orchestra)
            fileDownloadList.add(
                FileDownload(
                    fileName = orchestra?.vrFileName!!,
                    filePath = vrFile,
                    isDownloadComplete = false,
                    downLoadFileId = null,
                    fileSize = 0,
                    buttonType = null
                )
            )
        }
    }

    fun setOrchestraHallSoundData(orchestra: HallSoundResponse?) {
        this.orchestra = orchestra
        resetProgress()
        if (orchestra?.hallSound?.isEmpty() == true || orchestra?.hallSound?.size == 0) {
            ifViewAttached {
                it.hidePlayButton()
                it.hideDownloadButton()
            }
        } else {
            ifViewAttached {
                it.showPlayButton()
                //       it.showDownloadButton()
            }

            orchestra?.hallSound?.forEach {
                it.fileName = getFileName(it)
                fileDownloadList.add(
                    FileDownload(
                        fileName = it.fileName!!,
                        filePath = it.audioFile,
                        isDownloadComplete = false,
                        downLoadFileId = null,
                        fileSize = 0,
                        buttonType = null
                    )
                )
            }
        }
    }

    fun getDownloadedFilePath() {
        fileDownloadList.forEach {
            ifViewAttached { view ->
                downloadInteractor.getFileDownload(
                    compositeDisposable, view.getAppDatabase(), it?.fileName
                ).getSubscription()?.subscribe { list ->
                    if (list.isNotEmpty()) {
                        updateFile(it, list[0])
                        getDownloadSuccess(it)
                    }
                }?.let {
                    compositeDisposable?.add(it)
                }
            }
        }
    }

    private fun updateFile(oldFile: FileDownload?, newFile: FileDownload) {
        oldFile?.fileName = newFile.fileName
        oldFile?.filePath = newFile.filePath
        oldFile?.fileSize = newFile.fileSize
        oldFile?.downLoadFileId = newFile.downLoadFileId
        oldFile?.isDownloadComplete = newFile.isDownloadComplete
        oldFile?.buttonType = newFile.buttonType
    }

    fun playVideo(context: Context?) {
        if (orchestra == null || isVrFileEmpty() == true) {
            ifViewAttached { it.showMessageDialog(R.string.empty_video) }
            return
        }

        if (fileDownloadList.size > 0) {
            if (isFileDownloaded()) navigateToPlayer(0)
            else downloadFiles(context, Constants.PLAY_BUTTON, false)
        }
    }

    fun playAudio(context: Context?, position: Int) {
        if (orchestra == null || isAudioFilePathEmpty() == true) {
            ifViewAttached { it.showMessageDialog(R.string.empty_video) }
            return
        }

        if (fileDownloadList.size > position) {
            orchestra?.seatPosition = orchestra?.hallSound?.get(position)?.type?.trim()?.lowercase()
            orchestra?.musicTag = orchestra?.hallSound?.get(position)?.position

            if (isFileDownloaded(position) == true) navigateToPlayer(position)
            else downloadFile(fileDownloadList[position], context, Constants.PLAY_BUTTON)
        }
    }

    fun checkAudiosInDownloading(
        context: Context?, it: FileDownload?
    ) {
        downloadStateRetriever.isInDownloadList(fileDownloadList)
            .subscribeOn(Schedulers.newThread()).observeOn(AndroidSchedulers.mainThread())
            .subscribe({ isDownloading ->
                if (isDownloading) {
                    ifViewAttached { it.showMessage("Please wait. File is being download.") }
                } else downloadFile(it, context, Constants.PLAY_BUTTON, false)
            }, {
                Log.d("Br", it.message.toString())
            })?.let { compositeDisposable?.add(it) }
    }

    fun downloadVideo(context: Context?, buttonType: Int? = Constants.DOWNLOAD_BUTTON) {
        if (orchestra == null || isVrFileEmpty() == true) {
            ifViewAttached { it.showMessageDialog(R.string.empty_video) }
            return
        }

        downloadFiles(context, buttonType)
    }

    fun downloadHallSound(context: Context?) {
        if (orchestra == null || isAudioFilePathEmpty() == true) {
            ifViewAttached { it.showMessageDialog(R.string.empty_video) }
            return
        }

        downloadFiles(context, Constants.DOWNLOAD_BUTTON)
    }

    fun cancelDownload() {
        fileDownloadList.forEach {
            it?.downLoadFileId?.let { id ->
                if (it.isDownloadComplete == false) fileDownloader.stopDownload(id)
            }
        }
    }

    private fun downloadFiles(
        context: Context?, buttonType: Int?, cancelDownload: Boolean = true, position: Int? = null
    ) {
        if (position != null) {
            if (fileDownloadList.size > position) downloadFile(
                fileDownloadList[position], context, buttonType, cancelDownload
            )
        } else fileDownloadList.forEach {
            downloadFile(it, context, buttonType, cancelDownload)
        }
    }

    private fun downloadFile(
        it: FileDownload?, context: Context?, buttonType: Int?, cancelDownload: Boolean = true
    ) {
        if (it?.isDownloadComplete == false) {
            if (it.downLoadFileId != null) checkIsInDownloadList(it, {
                startFileDownload(
                    context, it.filePath, it.fileName, buttonType
                )
            }, cancelDownload)
            else startFileDownload(
                context, it.filePath, it.fileName, buttonType
            )
        } else progressList.add(
            DownloadProgress(
                downloadId = it?.downLoadFileId!!, fileSize = it.fileSize, progress = it.fileSize
            )
        )
    }

    private fun isFileDownloaded() =
        fileDownloadList.find { it?.isDownloadComplete == false } == null

    private fun isFileDownloaded(position: Int) = fileDownloadList[position]?.isDownloadComplete

    private fun isVrFileEmpty() = orchestra?.vrFile?.isEmpty()

    private fun isAudioFilePathEmpty() = orchestra?.hallSound?.isEmpty()

    private fun getFileLocalPath(context: Context?, fileName: String?): String? {
        val filePath =
            context?.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS)?.absolutePath + "/$fileName"
        val file = File(filePath)
        if (file.exists()) {
            fileDownloader.changeFileName(fileName)
        }

        val downloadedFilePath =
            context?.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS)?.absolutePath + "/downloaded-$fileName"
        val downloadedFile = File(downloadedFilePath)
        return if (downloadedFile.exists()) downloadedFilePath
        else null
    }


    private fun getDownloadSuccess(fileDownload: FileDownload?) {
        if (fileDownload?.isDownloadComplete == false) checkIsInDownloadList(
            fileDownload, {
                isNotInDownloadList(fileDownload)
                          },
            false
        )
        else checkAllDownloaded()
    }

    private fun checkAllDownloaded() {
//        if (isFileDownloaded()) {
        ifViewAttached {
            it.hideDownloadButton()
            it.setProgress(0)
            it.hideProgress()
        }
//        }
    }

    private fun checkIsInDownloadList(
        fileDownload: FileDownload, isNotDownloading: () -> Unit, cancelDownload: Boolean
    ) {
        fileDownload.downLoadFileId?.let {
            downloadStateRetriever.isInDownloadList(it).subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread()).subscribe { isDownloading ->
                    if (isDownloading) {
                        if (cancelDownload) fileDownloader.stopDownload(it)
                        else {
                            showProgressBar(it)
                        }
                    } else isNotDownloading()
                }?.let { compositeDisposable?.add(it) }
        }
    }

    private fun isNotInDownloadList(fileDownload: FileDownload) {
        fileDownload.downLoadFileId?.let { id ->
            if (downloadStateRetriever.isDownloaded(id)) {
                fileDownload.isDownloadComplete = true
                updateFileDownload(fileDownload)
                updateDownloadCount()
            } else deleteFileDownload(fileDownload)
        }
    }

    private fun updateFileDownload(fileDownload: FileDownload) {
        ifViewAttached {
            downloadInteractor.updateFileDownload(fileDownload, it.getAppDatabase())
                .getSubscription()?.subscribe()?.let { compositeDisposable?.add(it) }
        }
    }

    private fun deleteFileDownload(fileDownload: FileDownload) {
        ifViewAttached { v ->
            downloadInteractor.deleteFileDownload(fileDownload, v.getAppDatabase())
                .getSubscription()?.subscribe()?.let { compositeDisposable?.add(it) }
        }
    }

    private fun showProgressBar(id: Long) {
        ifViewAttached {
            it.showProgress()
            it.showStopDownloadButton()
        }
        downloadStateRetriever.getProgress(id, compositeDisposable).subscribeOn(Schedulers.io())
            ?.observeOn(AndroidSchedulers.mainThread())
            ?.subscribe({ setProgress(it) }, { exception ->
                ifViewAttached {
                    it.setProgress(0)
                    it.hideProgress()
                    it.hideDownloadButton()
                    if (exception is BaseRepository.DiskFullError) {
                        it.showMessageDialog(exception.message.toString())
                    }
                }
            }, {
                checkAllDownloaded()
            })?.let { compositeDisposable?.add(it) }
    }

    private fun setProgress(progress: DownloadProgress) {
        val lProgress = progressList.find { it.downloadId == progress.downloadId }
        if (lProgress != null) {
            lProgress.progress = progress.progress
            lProgress.fileSize = progress.fileSize
        } else progressList.add(progress)

        updateDownload(progress)

        ifViewAttached { it.setProgress(getProgress(progress.downloadId)) }
    }

    private fun getProgress(downloadId: Long): Int {
        val progress = progressList.find { it.downloadId == downloadId }
        return if (progress == null) 0 else ((progress.progress * 100F) / progress.fileSize).toInt()
    }

    private fun getAverageProgress(): Int {
        var totalSize = 0
        var downloadedSize = 0
        progressList.forEach {
            totalSize += it.fileSize

            downloadedSize += it.progress
        }

        return if (totalSize <= 0) 0 else ((downloadedSize * 100F) / totalSize).toInt()
    }

    private fun getFileName(orchestra: HallSoundResponse?): String {
        val strSplit = orchestra?.vrFile?.split("?Expires")?.get(0)?.split("/")
        return if (strSplit?.isNotEmpty() == true) strSplit[strSplit.size - 1]
        else ""
    }

    private fun getFileName(audioFile: HallSoundAudio?): String {
        val strSplit = audioFile?.audioFile?.split("?Expires")?.get(0)?.split("/")
        return if (strSplit?.isNotEmpty() == true) strSplit[strSplit.size - 1]
        else ""
    }

    private fun isInList(fileDownload: FileDownload?) =
        fileDownloadList.find { it?.fileName == fileDownload?.fileName } != null

    private fun updateDownload(progress: DownloadProgress) {
        val downloadFile = fileDownloadList.find { it?.downLoadFileId == progress.downloadId }
        if (downloadFile != null && downloadFile.fileSize <= 0) {
            downloadFile.fileSize = progress.fileSize
            updateFileDownload(downloadFile)
        }
    }

    private fun  updateDownloadList(fileDownload: FileDownload?) {
        val oldFile = fileDownloadList.find { it?.fileName == fileDownload?.fileName }
        val index = fileDownloadList.indexOf(oldFile)
        if (index >= 0) {
            fileDownloadList.remove(oldFile)
            fileDownloadList.add(index, fileDownload)
        }
    }

    private fun navigateToPlayer(position: Int) {
        if (fileDownloadList.isEmpty()) return
        ifViewAttached { view ->
            val filePath = getFileLocalPath(view.getContext(), fileDownloadList[position]?.fileName)
            if (filePath == null) downloadInteractor.deleteFileDownload(
                fileDownloadList[position], view.getAppDatabase()
            ).getSubscription()?.subscribe({
                fileDownloadList.removeAt(position)
                fileDownloadList.add(position, it)
                downloadFile(it, view.getContext(), it.buttonType, false)
            }, {})?.let { compositeDisposable?.add(it) }
            else view.navigateToPlayer(
                orchestra, filePath, fileDownloadList[position]?.fileName
            )
        }

    }

    private fun startFileDownload(
        context: Context?, file: String?, fileName: String, buttonType: Int?
    ) {
        if (!NetworkUtils.isNetworkAvailable(context)) {
            ifViewAttached { it.showMessageDialog(R.string.error_no_internet_connection) }
            return
        }
        if (!validateDownloadNetworkStatus(context)) {
            return
        }
        if (!PreferenceUtils.getDownloadOnlyOnWIFI(context) && PreferenceUtils.getNotifyingWhenMobileData(
                context
            ) && !NetworkUtils.isWIFINetworkAvailable(context)
        ) {
            DialogUtils.showAlertDialog(context,
                context?.getString(R.string.text_cellular_download_warning),
                {
                    fileDownloader.downloadFile(
                        file, fileName, buttonType
                    )
                },
                {

                },
                isCancelable = false,
                showNegativeBtn = true
            )
        } else {
            fileDownloader.downloadFile(
                file, fileName, buttonType
            )
        }
    }

    private fun validateDownloadNetworkStatus(context: Context?): Boolean {
        if (PreferenceUtils.getDownloadOnlyOnWIFI(context) && !NetworkUtils.isWIFINetworkAvailable(
                context
            )
        ) {
            ifViewAttached { it.showMessageDialog(R.string.text_wifi_download_only) }
            return false
        }
        return true
    }

    private fun getPosition(fileDownload: FileDownload) = fileDownloadList.indexOf(fileDownload)

    private var downloadCompleteBroadCast = object : BroadcastReceiver() {
        override fun onReceive(p0: Context?, intent: Intent?) {
            val fileDownload =
                intent?.getSerializableExtra(BundleConstant.downloadedFile) as? FileDownload
            if (isInList(fileDownload)) {
                updateDownloadList(fileDownload)
                if (fileDownload?.isDownloadComplete == true) {
                    if (fileDownload.buttonType == Constants.PLAY_BUTTON) navigateToPlayer(
                        getPosition(fileDownload)
                    )
                    //  updateDownloadCount()
                } else fileDownload?.downLoadFileId?.let { showProgressBar(it) }
            }
        }
    }

    private fun updateDownloadCount() {
        downloadInteractor.updateDownloadCount().subscribe({}, {})
    }

    fun onDestroy(context: Context) {
        LocalBroadcastManager.getInstance(context).unregisterReceiver(downloadCompleteBroadCast)
    }
}