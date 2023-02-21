package com.co.brasso.feature.landing.myPage.tab.setting.dataManagement

import com.co.brasso.database.room.AppDatabase
import com.co.brasso.feature.download.FileDownloadRepository
import io.reactivex.disposables.CompositeDisposable

class DataManagementFragmentInteractor {
    fun getCompletedFileDownloads(
        compositeDisposable: CompositeDisposable?,
        appDatabase: AppDatabase?

    ) = FileDownloadRepository.getCompletedFileDownloads(
        compositeDisposable,
        appDatabase
    )
}