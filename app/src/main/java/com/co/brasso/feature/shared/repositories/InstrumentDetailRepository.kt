package com.co.brasso.feature.shared.repositories

import com.co.brasso.feature.shared.base.BaseRepository
import com.co.brasso.feature.shared.model.response.sessionlayout.InstrumentDetailResponse
import com.co.brasso.utils.extension.getSubscription
import io.reactivex.Single
import io.reactivex.disposables.CompositeDisposable

object InstrumentDetailRepository : BaseRepository() {

    fun getInstrumentDetail(
        compositeDisposable: CompositeDisposable?,
        instrumentID: Int?,
        sessionID: Int?,
        musicianId:Int?,
        token: String?,
        videoSupport:String?
    ): Single<InstrumentDetailResponse> = Single.create { e ->
        apiService?.getInstrumentDetail(token,videoSupport, instrumentID, sessionID,musicianId)
            .getSubscription()
            ?.subscribe({
                if (it.isSuccessful && it.body()?.data != null) {
                    e.onSuccess(it.body()?.data ?: InstrumentDetailResponse())
                } else {
                    e.onError(getError(it.code(), it.errorBody()?.string()))
                }
            }, {
                e.onError(getDefaultError(it))
            })?.let {
                compositeDisposable?.add(it)
            }
    }
}