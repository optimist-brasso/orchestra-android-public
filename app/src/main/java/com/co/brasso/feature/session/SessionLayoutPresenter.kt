package com.co.brasso.feature.session

import com.co.brasso.R
import com.co.brasso.feature.session.sessionDetail.sessionDetail.SessionDetailInteractor
import com.co.brasso.feature.shared.base.BasePresenter
import com.co.brasso.feature.shared.model.response.sessionBoughtStatus.SessionBoughtStatus

class SessionLayoutPresenter : BasePresenter<SessionLayoutView>() {
    private var sessionLayoutInteractor: SessionLayoutInteractor? = null
    private var sessionDetailInteractor: SessionDetailInteractor? = null

    override fun attachView(view: SessionLayoutView) {
        super.attachView(view)
        sessionLayoutInteractor = SessionLayoutInteractor()
        sessionDetailInteractor = SessionDetailInteractor()
    }

    override fun detachView() {
        sessionLayoutInteractor = null
        sessionDetailInteractor = null
        super.detachView()
    }

    fun getInstrumentDetail(
        instrumentID: Int?, sessionID: Int?, musicianId: Int?, videoSupport: String?
    ) {
        ifViewAttached { view ->
            if (!view.isNetworkAvailable()) {
                view.showMessageDialog(R.string.error_no_internet_connection)
                return@ifViewAttached
            }
            view.showLoading()
            getToken().flatMap {
                sessionDetailInteractor?.getInstrumentDetail(
                    compositeDisposable, instrumentID, sessionID, musicianId, it, videoSupport
                )
            }.subscribe({
                view.hideLoading()
                if (!it.vrFile.isNullOrEmpty()) view.navigateToDetail()
                else
                    view.navigateToPlayerDetail(musicianId)

            }, {
                view.hideLoading()
                showErrorView(it) {}
            }).let {
                compositeDisposable?.add(it)
            }
        }
    }

    fun getSessionLayout(sessionId: Int?) {
        ifViewAttached { view ->
            if (view.isNetworkAvailable()) {
                view.showLoading()
                getToken().flatMap {
                    sessionLayoutInteractor?.getSessionLayout(
                        compositeDisposable, it, sessionId ?: 0
                    )
                }.subscribe({
                    view.hideLoading()
                    view.setSessionLayout(it)
                }, {
                    view.hideLoading()
                    showErrorView(it) {
                        view.popUpBackStack()
                    }
                }).let {
                    compositeDisposable?.add(it)
                }
            } else view.showMessageDialog(R.string.error_no_internet_connection)
        }
    }

    fun getSessionLayoutForGuestLogin(sessionId: Int?) {
        ifViewAttached { view ->
            if (view.isNetworkAvailable()) {
                view.showLoading()
                getToken().flatMap {
                    sessionLayoutInteractor?.getSessionLayoutForGuest(
                        compositeDisposable, it, sessionId ?: 0
                    )
                }.subscribe({
                    view.hideLoading()
                    view.setSessionLayout(SessionBoughtStatus(it))
                }, {
                    view.hideLoading()
                    showErrorView(it) {
                        view.popUpBackStack()
                    }
                }).let {
                    compositeDisposable?.add(it)
                }
            } else view.showMessageDialog(R.string.error_no_internet_connection)
        }
    }
}