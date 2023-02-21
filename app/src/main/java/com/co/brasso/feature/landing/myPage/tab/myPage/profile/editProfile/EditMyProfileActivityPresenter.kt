package com.co.brasso.feature.landing.myPage.tab.myPage.profile.editProfile

import com.co.brasso.R
import com.co.brasso.feature.shared.base.BasePresenter
import com.co.brasso.feature.shared.model.response.appinfo.AppInfoGlobal
import com.co.brasso.feature.shared.model.updateProfile.UpdateProfileEntity
import com.co.brasso.utils.constant.StringConstants
import java.io.File

class EditMyProfileActivityPresenter : BasePresenter<EditMyProfileActivityView>() {

    private var editMyProfileActivityInteractor: EditMyProfileActivityInteractor? = null

    override fun attachView(view: EditMyProfileActivityView) {
        super.attachView(view)
        editMyProfileActivityInteractor = EditMyProfileActivityInteractor()
    }

    override fun detachView() {
        editMyProfileActivityInteractor = null
        super.detachView()
    }

    fun updateProfile(updateProfileEntity: UpdateProfileEntity) {
        ifViewAttached { view ->
            if (view.isNetworkAvailable()) {
                if (validate(updateProfileEntity, view)) {
                    view.showLoading()
                    getToken().flatMap { token ->
                        editMyProfileActivityInteractor?.updateProfile(
                            compositeDisposable,
                            updateProfileEntity, token
                        )
                    }.subscribe({
                        view.hideLoading()
                        view.editProfileDetailSuccess()
                    }, {
                        view.hideLoading()
                        showErrorView(it) {}
                    }).let {
                        compositeDisposable?.add(it)
                    }
                }
            } else
                view.showMessageDialog(R.string.error_no_internet_connection)
        }
    }

    fun updateProfilePic(uploadProfileFile: File?) {
        ifViewAttached { view ->
            if (view.isNetworkAvailable()) {
                if (validateImageSize(uploadProfileFile,view)) {
                    view.showLoading()
                    getToken().flatMap { token ->
                        editMyProfileActivityInteractor?.updateProfilePic(
                            compositeDisposable,
                            uploadProfileFile, token
                        )
                    }.subscribe({
                        view.hideLoading()
                        view.editProfilePicSuccess(it)
                    }, {
                        view.hideLoading()
                        showErrorView(it) {}
                    }).let {
                        compositeDisposable?.add(it)
                    }
                }
            } else
                view.showMessageDialog(R.string.error_no_internet_connection)
        }
    }

    private fun validateImageSize(uploadProfileFile: File?, view: EditMyProfileActivityView): Boolean {
        if (uploadProfileFile != null) {
            val imageSizeInKB = uploadProfileFile.length().div(1024)
            if ((imageSizeInKB) > (AppInfoGlobal.appInfo?.configData?.profileImageSize?.toInt()
                    ?: 2048)
            ) {
                val errorText =
                    AppInfoGlobal.appInfo?.configData?.profileImageSize + StringConstants.fileSizeExceed
                view.showMessageDialog(errorText)
                return false
            }
        }
        return true
    }

    fun deleteProfilePic() {
        ifViewAttached { view ->
            if (view.isNetworkAvailable()) {
                view.showLoading()
                getToken().flatMap { token ->
                    editMyProfileActivityInteractor?.deleteProfilePic(
                        compositeDisposable,
                        token
                    )
                }.subscribe({
                    view.hideLoading()
                    view.deleteProfilePicSuccess()
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

    private fun validate(
        updateProfileEntity: UpdateProfileEntity?,
        view: EditMyProfileActivityView
    ): Boolean {
        if (updateProfileEntity?.username.isNullOrEmpty()) {
            view.showMessageDialog(R.string.nick_name_empty)
            return false
        }

        if (updateProfileEntity?.name.isNullOrEmpty()) {
            view.showMessageDialog(R.string.name_empty)
            return false
        }

        if (updateProfileEntity?.gender.isNullOrEmpty()) {
            view.showMessageDialog(R.string.select_gender)
            return false
        }

        if (updateProfileEntity?.musicalInstrument.isNullOrEmpty()) {
            view.showMessageDialog(R.string.empty_music_instrument)
            return false
        }

        if (updateProfileEntity?.dob.isNullOrEmpty()) {
            view.showMessageDialog(R.string.empty_dob)
            return false
        }

        if (updateProfileEntity?.postalCode.isNullOrEmpty()) {
            view.showMessageDialog(R.string.postal_code_empty)
            return false
        }

        if (updateProfileEntity?.professionalID.isNullOrEmpty()) {
            view.showMessageDialog(R.string.profession_empty)
            return false
        }

        return true
    }
}
