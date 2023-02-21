package com.co.brasso.feature.landing.myPage.tab.setting.dataManagement

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.os.Environment
import android.os.StatFs
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.NonNull
import com.co.brasso.R
import com.co.brasso.databinding.FragmentDataManagementBinding
import com.co.brasso.feature.download.FileDownload
import com.co.brasso.feature.shared.base.BaseFragment
import com.co.brasso.utils.extension.viewGone
import com.co.brasso.utils.extension.viewVisible
import com.co.brasso.utils.util.DialogUtils
import com.co.brasso.utils.util.Logger
import com.jakewharton.rxbinding3.view.clicks
import java.io.File
import java.math.RoundingMode
import java.text.DecimalFormat
import kotlin.math.log10
import kotlin.math.pow

class DataManagementFragment : BaseFragment<DataManagementFragmentView, DataManagementFragmentPresenter>(),
    DataManagementFragmentView {

    private lateinit var binding: FragmentDataManagementBinding
    private var downloadedVideos: MutableList<FileDownload>? = null

    override fun onCreateView(
        @NonNull inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentDataManagementBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setScreenPortrait()
        setup()
        presenter.getFileDownloads()
    }

    private fun setup() {
        initListeners()
        initializeCache()
    }

    private fun initListeners() {
        binding.btnDeleteCache.setOnClickListener {
            DialogUtils.showAlertDialog(
                requireContext(),
                getString(R.string.clear_cache_confirmation),
                { proceedToClearCache() },
                {},
                isCancelable = false,
                showNegativeBtn = true
            )
        }

        binding.btnDeleteDownloadData.clicks().subscribe {
            DialogUtils.showAlertDialog(
                requireContext(),
                getString(R.string.delete_download_confirmation),
                { presenter.proceedToDeleteDownload(getAppDatabase(), downloadedVideos) },
                {},
                isCancelable = false,
                showNegativeBtn = true
            )
        }?.let {
            compositeDisposable?.add(it)
        }
    }

    private fun proceedToClearCache() {
        deleteCache(requireContext())
    }

    override fun getFile(filePath: String?) =
        File(requireContext().getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS)?.absolutePath + "/downloaded-$filePath")


    private fun deleteCache(context: Context) {
        try {
            val dir: File = context.cacheDir
            deleteDir(dir)
        } catch (e: Exception) {
            e.printStackTrace()
        }
        initializeCache()
    }

    private fun deleteDir(dir: File?): Boolean {
        return if (dir != null && dir.isDirectory) {
            val children: Array<String> = dir.list() as Array<String>
            for (i in children.indices) {
                val success = deleteDir(File(dir, children[i]))
                if (!success) {
                    return false
                }
            }
            dir.delete()
        } else if (dir != null && dir.isFile) {
            dir.delete()
        } else {
            false
        }
    }

    override fun showProgressBar() {
        binding.png.cnsProgress.viewVisible()
    }

    override fun hideProgressBar() {
        binding.png.cnsProgress.viewGone()
    }

    @SuppressLint("SetTextI18n")
    override fun setDownloadVideoPath(downloadFiles: List<FileDownload>) {
        downloadedVideos = mutableListOf()
        var downloadedFileSize: Double? = 0.0
        var fileSuffix = " MB"
        var fileSize=0

        downloadFiles.forEach {
            val file=getFileLocalPath(context,it.fileName)
            if (file != null) {
                fileSize+= 1
                downloadedFileSize = downloadedFileSize?.plus((file.length().div(1024.0)).div(1024.0))
                downloadedVideos?.add(it)
            }
        }

        binding.txvDownloadComplete.text =
            fileSize.toString() + getString(R.string.songs)
        if ((downloadedFileSize ?: 0.0) > 1024.0) {
            downloadedFileSize = (downloadedFileSize)?.div(1024.0)
            fileSuffix = " GB"
        }
        val formattedSize=DecimalFormat("#.#").format(downloadedFileSize)
        binding.txvDownloadCapacityUsed.text =
            formattedSize.toString() + fileSuffix
        getInternalMemoryAvailable()
    }

    override fun createPresenter() = DataManagementFragmentPresenter()

    fun changeFileName(fileName: String?) {
        val file =
            File(context?.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS)?.absolutePath +  "/$fileName")

        if (file.exists()) {
            val newFile =
                File(context?.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS)?.absolutePath +  "/downloaded-$fileName")
            file.renameTo(newFile)
        }
    }

    private fun getFileLocalPath(context: Context?, fileName: String?): File? {
        val filePath =
            context?.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS)?.absolutePath + "/$fileName"
        val file = File(filePath)
        if (file.exists()) {
            changeFileName(fileName)
        }

        val downloadedFilePath =
            context?.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS)?.absolutePath + "/downloaded-$fileName"
        val downloadedFile = File(downloadedFilePath)
        return if (downloadedFile.exists())
            downloadedFile
        else null
    }

    private fun getInternalMemoryAvailable() {
        try {
            val iPath: File = Environment.getDataDirectory()
            val iStat = StatFs(iPath.path)
            val iBlockSize = iStat.blockSizeLong
            val iAvailableBlocks = iStat.availableBlocksLong
            val iAvailableSpace = formatSize((iAvailableBlocks * iBlockSize).toDouble())
            binding.txvFreeSpace.text = iAvailableSpace
        } catch (e: Exception) {
            Logger.d("jp", e.localizedMessage)
        }
    }

    private fun formatSize(availableSize: Double): String {
        var size = availableSize
        var suffix: String? = null
        if (size >= 1024) {
            suffix = " KB"
            size /= 1024
            if (size >= 1024) {
                suffix = " MB"
                size /= 1024
            }
            if (size >= 1024) {
                suffix = " GB"
                size /= 1024f
            }
        }
        val formattedSize = DecimalFormat("##.##").format(size)
        val resultBuffer = StringBuilder(formattedSize.toString())
        if (suffix != null) resultBuffer.append(suffix)
        return resultBuffer.toString()
    }

    private fun initializeCache() {
        var size: Long = 0
        size += getDirSize(requireContext().cacheDir)
        binding.txvCacheUsage.text = readableFileSize(size)
    }

    private fun getDirSize(dir: File?): Long {
        var size: Long = 0
        for (file in dir?.listFiles()!!) {
            if (file != null && file.isDirectory) {
                size += getDirSize(file)
            } else if (file != null && file.isFile) {
                size += file.length()
            }
        }
        return size
    }

    private fun readableFileSize(size: Long): String? {
        if (size <= 0) return "0 B"
        val units = arrayOf("B", "kB", "MB", "GB", "TB")
        val digitGroups = (log10(size.toDouble()) / log10(1024.0)).toInt()
        return DecimalFormat("#,##0.#").format(
            size / 1024.0.pow(digitGroups.toDouble())
        ) + " " + units[digitGroups]
    }
}