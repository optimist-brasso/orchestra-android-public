package com.co.brasso.feature.landing.myPage.tab.setting.dataManagement

import com.co.brasso.database.room.AppDatabase
import com.co.brasso.feature.download.FileDownload
import com.co.brasso.feature.download.FileDownloadRepository
import com.co.brasso.feature.shared.base.BasePresenter
import com.co.brasso.utils.constant.Constants
import com.co.brasso.utils.extension.getSubscription
import com.co.brasso.utils.util.Logger
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers


class DataManagementFragmentPresenter : BasePresenter<DataManagementFragmentView>() {
    private var dataManagementFragmentInteractor: DataManagementFragmentInteractor? = null

    override fun attachView(view: DataManagementFragmentView) {
        super.attachView(view)
        dataManagementFragmentInteractor = DataManagementFragmentInteractor()
    }

    override fun detachView() {
        dataManagementFragmentInteractor = null
        super.detachView()
    }

    fun getFileDownloads() {
        ifViewAttached { view ->
            view.showProgressBar()
            dataManagementFragmentInteractor?.getCompletedFileDownloads(
                compositeDisposable,
                view.getAppDatabase(),
            ).getSubscription()?.subscribe({
                view.hideProgressBar()
                view.setDownloadVideoPath(it)
            }, {
                view.hideProgressBar()
                Logger.d("jp", it?.localizedMessage ?: Constants.defaultErrorText)
            })?.let {
                compositeDisposable?.add(it)
            }
        }
    }

    private fun deleteFile(filePath: String?) {
        ifViewAttached {
            val file = it.getFile(filePath)
            if (file.exists())
                file.delete()
        }
    }

    fun proceedToDeleteDownload(
        appDatabase: AppDatabase?,
        downloadedVideos: MutableList<FileDownload>?
    ) {
        val list = mutableListOf<Single<FileDownload>>()
        downloadedVideos?.forEach {
            list.add(
                FileDownloadRepository.deleteFileDownloadDb(
                    appDatabase, it
                )
            )
        }
        Single.merge(list)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                if (it.fileName.isNotEmpty())
                    deleteFile(it.fileName)
            }, {}, {
                getFileDownloads()
            })?.let { compositeDisposable?.add(it) }
    }

}

