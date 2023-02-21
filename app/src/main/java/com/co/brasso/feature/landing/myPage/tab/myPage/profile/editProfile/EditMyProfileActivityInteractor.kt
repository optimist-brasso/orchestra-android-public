package com.co.brasso.feature.landing.myPage.tab.myPage.profile.editProfile

import com.co.brasso.feature.shared.model.updateProfile.UpdateProfileEntity
import com.co.brasso.feature.shared.repositories.EditMyProfileRepository
import io.reactivex.disposables.CompositeDisposable
import java.io.File

class EditMyProfileActivityInteractor {

    fun updateProfile(
        compositeDisposable: CompositeDisposable?,
        updateProfileEntity: UpdateProfileEntity,
        token: String
    ) = EditMyProfileRepository.updateProfile(compositeDisposable, updateProfileEntity, token)

    fun updateProfilePic(
        compositeDisposable: CompositeDisposable?,
        updateProfilePic: File?,
        token: String
    ) = EditMyProfileRepository.updateProfilePic(compositeDisposable, updateProfilePic, token)

    fun deleteProfilePic(
        compositeDisposable: CompositeDisposable?,
        token: String
    ) = EditMyProfileRepository.deleteProfilePic(compositeDisposable, token)

}
