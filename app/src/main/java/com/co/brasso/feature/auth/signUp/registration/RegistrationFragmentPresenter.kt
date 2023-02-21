package com.co.brasso.feature.auth.signUp.registration

import com.co.brasso.R
import com.co.brasso.feature.landing.myPage.tab.myPage.profile.editProfile.EditMyProfileActivityInteractor
import com.co.brasso.feature.shared.base.BasePresenter
import com.co.brasso.feature.shared.model.emailLogin.EmailLoginEntity
import com.co.brasso.feature.shared.model.registration.UserRegistrationEntity
import com.co.brasso.feature.shared.model.response.appinfo.AppInfoGlobal
import com.co.brasso.utils.constant.StringConstants

class RegistrationFragmentPresenter :
    BasePresenter<RegistrationFragmentView>() {
    private var registrationFragmentInteractor: RegistrationFragmentInteractor? = null
    private var editMyProfileFragmentInteractor: EditMyProfileActivityInteractor? = null

    override fun attachView(view: RegistrationFragmentView) {
        super.attachView(view)
        registrationFragmentInteractor = RegistrationFragmentInteractor()
        editMyProfileFragmentInteractor = EditMyProfileActivityInteractor()
    }

    override fun detachView() {
        registrationFragmentInteractor = null
        editMyProfileFragmentInteractor = null
        super.detachView()
    }

    fun deleteProfilePic() {
        ifViewAttached { view ->
            if (view.isNetworkAvailable()) {
                view.showLoading()
                getToken().flatMap { token ->
                    editMyProfileFragmentInteractor?.deleteProfilePic(
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

    fun register(userRegistrationEntity: UserRegistrationEntity) {
        ifViewAttached { view ->
            if (view.isNetworkAvailable()) {
                if (validate(userRegistrationEntity, view)) {
                    view.showLoading()
                    getToken().flatMap {
                        registrationFragmentInteractor?.register(
                            compositeDisposable,
                            userRegistrationEntity,
                            it
                        )
                    }.subscribe({
                        view.hideLoading()
                        view.success(it.message)
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

    private fun validate(
        userRegistrationEntity: UserRegistrationEntity?,
        view: RegistrationFragmentView
    ): Boolean {

        if (userRegistrationEntity?.profileImage != null) {
            val imageSizeInKB = userRegistrationEntity.profileImage?.length()?.div(1024)
            if ((imageSizeInKB
                    ?: 2048) > (AppInfoGlobal.appInfo?.configData?.profileImageSize?.toInt()
                    ?: 2048)
            ) {
                val errorText =
                    AppInfoGlobal.appInfo?.configData?.profileImageSize + StringConstants.fileSizeExceed
                view.showMessageDialog(errorText)
                return false
            }
        }

        if (userRegistrationEntity?.name.isNullOrEmpty()) {
            view.showMessageDialog(R.string.name_empty)
            return false
        }

        if (userRegistrationEntity?.username.isNullOrEmpty()) {
            view.showMessageDialog(R.string.nick_name_empty)
            return false
        }

        if (userRegistrationEntity?.gender.isNullOrEmpty()) {
            view.showMessageDialog(R.string.select_gender)
            return false
        }

        if (userRegistrationEntity?.musicInstrument.isNullOrEmpty()) {
            view.showMessageDialog(R.string.empty_music_instrument)
            return false
        }

        if (userRegistrationEntity?.postalCode.isNullOrEmpty()) {
            view.showMessageDialog(R.string.postal_code_empty)
            return false
        }

        if (userRegistrationEntity?.dob.isNullOrEmpty()) {
            view.showMessageDialog(R.string.empty_dob)
            return false
        }

        if (userRegistrationEntity?.professionalID.isNullOrEmpty()) {
            view.showMessageDialog(R.string.profession_empty)
            return false
        }

        return true
    }

    fun emailLogin(emailLoginEntity: EmailLoginEntity) {
        ifViewAttached { view ->
            if (view.isNetworkAvailable()) {
                view.showLoading()
                registrationFragmentInteractor?.emailLogin(compositeDisposable, emailLoginEntity)
                    ?.subscribe({
                        view.hideLoading()
                        view.saveToken(it)
                        view.setLoginType(StringConstants.emailLogin)
                        view.navigateToMain()
                    }, {
                        view.hideLoading()
                        showErrorView(it) {}
                    })?.let {
                        compositeDisposable?.add(it)
                    }
            } else
                view.showMessageDialog(R.string.error_no_internet_connection)
        }
    }
}