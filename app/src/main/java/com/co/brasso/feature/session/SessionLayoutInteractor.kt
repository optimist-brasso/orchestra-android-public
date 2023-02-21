package com.co.brasso.feature.session

import com.co.brasso.feature.shared.base.BaseInteractor
import com.co.brasso.feature.shared.model.response.sessionBoughtStatus.SessionBoughtStatus
import com.co.brasso.feature.shared.model.response.sessionlayout.SessionLayoutResponse
import com.co.brasso.feature.shared.repositories.MultiPartListRepository
import com.co.brasso.feature.shared.repositories.SessionLayoutRepository
import io.reactivex.Single
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.functions.BiFunction


class SessionLayoutInteractor : BaseInteractor() {

    fun getSessionLayout(
        compositeDisposable: CompositeDisposable?,
        token: String,
        sessionId: Int,
    ): Single<SessionBoughtStatus> = Single.zip(
        SessionLayoutRepository.getSessionLayout(compositeDisposable, sessionId,token),
        MultiPartListRepository.getMultiPartList(compositeDisposable, sessionId, token),
        BiFunction { success, products ->
            SessionBoughtStatus(success, products)
        }

    )

    fun getSessionLayoutForGuest(
        compositeDisposable: CompositeDisposable?,
        token: String,
        sessionId: Int,
    ): Single<SessionLayoutResponse> =
        SessionLayoutRepository.getSessionLayout(compositeDisposable, sessionId,token)



}