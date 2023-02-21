package com.co.brasso.feature.landing.myPage.tab.myPage.recordList

import com.co.brasso.R
import com.co.brasso.feature.shared.base.BasePresenter

class RecordingFragmentPresenter : BasePresenter<RecordingFragmentView>() {

    private var recordingFragmentInteractor: RecordingFragmentInteractor? = null

    override fun attachView(view: RecordingFragmentView) {
        super.attachView(view)
        recordingFragmentInteractor = RecordingFragmentInteractor()
    }

    override fun detachView() {
        recordingFragmentInteractor = null
        super.detachView()
    }

    fun getRecordList(slug: String?, page: Int?) {
        ifViewAttached { view ->
            if (view.isNetworkAvailable()) {
                if (page == 1)
                    view.showProgressBar()
                getToken().flatMap {
                    recordingFragmentInteractor?.getRecording(compositeDisposable, it, slug, page)
                }.subscribe({
                    if (page == 1)
                        view.hideProgressBar()
                    view.setRecordingList(it.toMutableList())
                }, {
                    if (page == 1)
                        view.hideProgressBar()
                    showErrorView(it) {}
                }).let {
                    compositeDisposable?.add(it)
                }
            } else
                view.showMessageDialog(R.string.error_no_internet_connection)
        }
    }

    fun getRecordListOnPullToRefresh(slug: String?, page: Int?) {
        ifViewAttached { view ->
            if (view.isNetworkAvailable()) {
                view.showLoading()
                getToken().flatMap {
                    recordingFragmentInteractor?.getRecording(compositeDisposable, it, slug, page)
                }.subscribe({
                    view.hideLoading()
                    view.setRecordingList(it.toMutableList())
                }, {
                    view.hideLoading()
                    showErrorView(it) {}
                }).let {
                    compositeDisposable?.add(it)
                }
            } else
                view.showMessageDialog(R.string.error_no_internet_connection)
        }
    }
}