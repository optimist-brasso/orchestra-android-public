package com.co.brasso.feature.shared.repositories

import com.co.brasso.feature.shared.base.BaseRepository
import com.co.brasso.feature.shared.model.response.recordingList.RecordingListResponse
import com.co.brasso.utils.extension.getSubscription
import io.reactivex.Single
import io.reactivex.disposables.CompositeDisposable

object RecordingListRepository : BaseRepository() {

    fun getRecording(
        compositeDisposable: CompositeDisposable?,
        token: String?,
        slug: String?,
        page: Int?
    ): Single<List<RecordingListResponse>> = Single.create { e ->
        apiService?.getRecording(token,slug,page)
            .getSubscription()
            ?.subscribe({ response ->
                if (response.isSuccessful && response.body()?.data != null) {
                    val players = response?.body()?.data
                    val pagination = response?.body()?.meta?.pagination
                    players?.forEach {
                        it.hasNext =
                            (pagination?.page ?: 0) < (pagination?.totalPages ?: 0)
                    }
                    e.onSuccess(players ?: listOf())
                } else {
                    e.onError(getError(response.code(), response.errorBody()?.string()))
                }
            }, {
                e.onError(getDefaultError(it))
            })?.let {
                compositeDisposable?.add(it)
            }
    }
}