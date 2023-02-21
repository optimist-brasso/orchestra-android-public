package com.co.brasso.feature.shared.repositories

import com.co.brasso.feature.shared.base.BaseRepository
import com.co.brasso.feature.shared.model.response.myprofile.MyProfileResponse
import com.co.brasso.feature.shared.model.response.resendotp.SuccessResponse
import com.co.brasso.feature.shared.model.updateProfile.UpdateProfileEntity
import com.co.brasso.utils.constant.ApiConstant
import com.co.brasso.utils.extension.getSubscription
import com.co.brasso.utils.extension.toMultiPartRequestBody
import io.reactivex.Single
import io.reactivex.disposables.CompositeDisposable
import java.io.File

object EditMyProfileRepository : BaseRepository() {

    fun updateProfile(
        compositeDisposable: CompositeDisposable?,
        updateProfileEntity: UpdateProfileEntity,
        token: String
    ): Single<MyProfileResponse> = Single.create { e ->
        apiService?.updateProfile(token, updateProfileEntity)
            .getSubscription()
            ?.subscribe({
                if (it.isSuccessful) {
                    e.onSuccess(it.body()?.data ?: MyProfileResponse())
                } else {
                    e.onError(getError(it.code(),it.errorBody()?.string()))
                }
            }, {
                e.onError(getDefaultError(it))
            })?.let {
                compositeDisposable?.add(it)
            }
    }

    fun updateProfilePic(
        compositeDisposable: CompositeDisposable?,
        updateProfilePic: File?,
        token: String
    ): Single<MyProfileResponse> = Single.create { e ->
        val profileImageMultiPart = updateProfilePic?.toMultiPartRequestBody(
            "image/*",
            ApiConstant.profileImage
        )
        apiService?.updateProfilePic(token, profileImageMultiPart)
            .getSubscription()
            ?.subscribe({
                if (it.isSuccessful) {
                    e.onSuccess(it.body()?.data ?: MyProfileResponse())
                } else {
                    e.onError(getError(it.code(),it.errorBody()?.string()))
                }
            }, {
                e.onError(getDefaultError(it))
            })?.let {
                compositeDisposable?.add(it)
            }
    }

    fun deleteProfilePic(
        compositeDisposable: CompositeDisposable?,
        token: String
    ): Single<SuccessResponse> = Single.create { e ->
        apiService?.deleteProfilePic(token)
            .getSubscription()
            ?.subscribe({
                if (it.isSuccessful) {
                    e.onSuccess(it.body()?.data ?: SuccessResponse())
                } else {
                    e.onError(getError(it.code(),it.errorBody()?.string()))
                }
            }, {
                e.onError(getDefaultError(it))
            })?.let {
                compositeDisposable?.add(it)
            }
    }

}