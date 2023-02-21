package com.co.brasso.feature.landing.myPage.tab.setting.dataManagement

import com.co.brasso.feature.download.FileDownload
import com.co.brasso.feature.shared.base.BaseView
import java.io.File

interface DataManagementFragmentView : BaseView {
    fun showProgressBar()
    fun hideProgressBar()
    fun setDownloadVideoPath(downloadFiles: List<FileDownload>)
    fun getFile(filePath: String?):File
}