package com.co.brasso.feature.shared.repositories

import com.co.brasso.feature.shared.base.BaseRepository
import com.co.brasso.feature.shared.model.response.hallsound.HallSoundResponse
import com.co.brasso.utils.extension.getSubscription
import io.reactivex.Single
import io.reactivex.disposables.CompositeDisposable

object OrchestraRepository : BaseRepository() {
    fun getOrchestra(
        compositeDisposable: CompositeDisposable?,
        token: String
    ): Single<List<HallSoundResponse>> =
        Single.create { e ->
            apiService?.getOrchestra(token)
                .getSubscription()
                ?.subscribe({
                    if (it.isSuccessful && it.body()?.data != null) {
                        e.onSuccess(it.body()?.data ?: listOf())
                    } else {
                        e.onError(HomeRepository.getError(it.code(), it.errorBody()?.string()))
                    }
                }, {
                    e.onError(HomeRepository.getDefaultError(it))
                })?.let {
                    compositeDisposable?.add(it)
                }
        }

    fun getOrchestraDetail(
        compositeDisposable: CompositeDisposable?,
        token: String,
        id: Int?,
        videoSupport:String?
    ): Single<HallSoundResponse> =
        Single.create { e ->
            apiService?.getOrchestraDetail(token,videoSupport, id)
                .getSubscription()
                ?.subscribe({
                    if (it.isSuccessful && it.body()?.data != null) {
                        e.onSuccess(it.body()?.data!!)
                    } else {
                        e.onError(HomeRepository.getError(it.code(), it.errorBody()?.string()))
                    }
                }, {
                    e.onError(HomeRepository.getDefaultError(it))
                })?.let {
                    compositeDisposable?.add(it)
                }
        }
}