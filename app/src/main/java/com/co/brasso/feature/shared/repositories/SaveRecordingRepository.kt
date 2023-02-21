package com.co.brasso.feature.shared.repositories

import com.co.brasso.feature.shared.base.BaseRepository
import com.co.brasso.feature.shared.model.recording.RecordingEntity
import com.co.brasso.feature.shared.model.response.registration.RegistrationResponse
import com.co.brasso.utils.constant.ApiConstant
import com.co.brasso.utils.extension.getSubscription
import com.co.brasso.utils.extension.toMultiPartRequestBody
import com.co.brasso.utils.util.Utils
import io.reactivex.Single
import io.reactivex.disposables.CompositeDisposable

object SaveRecordingRepository : BaseRepository() {

    fun saveRecording(
        compositeDisposable: CompositeDisposable?,
        recordingEntity: RecordingEntity,
        token: String?
    ): Single<RegistrationResponse> = Single.create { e ->
        val recordingFormData = Utils.recordingPostHashMap(recordingEntity)
        val recordingFileMultiPart = recordingEntity.recordingFile?.toMultiPartRequestBody(
            "file/*",
            ApiConstant.recordingFile
        )
        apiService?.saveRecording(token, recordingFormData, recordingFileMultiPart)
            .getSubscription()
            ?.subscribe({
                if (it.isSuccessful) {
                    e.onSuccess(it.body()?.data ?: RegistrationResponse())
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