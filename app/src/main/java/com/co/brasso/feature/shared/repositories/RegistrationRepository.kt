package com.co.brasso.feature.shared.repositories

import com.co.brasso.feature.shared.base.BaseRepository
import com.co.brasso.feature.shared.model.registration.EmailPasswordRegister
import com.co.brasso.feature.shared.model.registration.UserRegistrationEntity
import com.co.brasso.feature.shared.model.response.registration.RegistrationResponse
import com.co.brasso.feature.shared.model.response.resendotp.SuccessResponse
import com.co.brasso.utils.constant.ApiConstant
import com.co.brasso.utils.extension.getSubscription
import com.co.brasso.utils.extension.toJson
import com.co.brasso.utils.extension.toMultiPartRequestBody
import com.co.brasso.utils.util.Utils
import io.reactivex.Single
import io.reactivex.disposables.CompositeDisposable

object RegistrationRepository : BaseRepository() {

    fun register(
        compositeDisposable: CompositeDisposable?,
        userRegistrationEntity: UserRegistrationEntity,
        token:String?
    ): Single<RegistrationResponse> = Single.create { e ->
        val registerFormData = Utils.registerPostHashMap(userRegistrationEntity)
        val profileImageMultiPart = userRegistrationEntity.profileImage?.toMultiPartRequestBody(
            "image/*",
            ApiConstant.profileImage
        )
        apiService?.register(registerFormData, profileImageMultiPart,token)
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


    fun emailPasswordRegister(
        compositeDisposable: CompositeDisposable?,
        userRegistrationEntity: EmailPasswordRegister
    ): Single<SuccessResponse> = Single.create { e ->
        apiService?.userEmailPasswordRegister(userRegistrationEntity.toJson())
            .getSubscription()
            ?.subscribe({
                if (it.isSuccessful) {
                    e.onSuccess(it.body()?.data ?: SuccessResponse())
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