package com.co.brasso.feature.landing.myPage.tab.myPage.changePassword

import com.co.brasso.R
import com.co.brasso.feature.shared.base.BasePresenter
import com.co.brasso.feature.shared.model.request.auth.ChangePasswordVerification
import com.co.brasso.utils.constant.StringConstants
import java.util.regex.Matcher
import java.util.regex.Pattern

class ChangePasswordFragmentPresenter : BasePresenter<ChangePasswordFragmentView>() {

    private var changePasswordFragmentInteractor: ChangePasswordFragmentInteractor? = null

    override fun attachView(view: ChangePasswordFragmentView) {
        super.attachView(view)
        changePasswordFragmentInteractor = ChangePasswordFragmentInteractor()
    }

    override fun detachView() {
        changePasswordFragmentInteractor = null
        super.detachView()
    }

    fun changePassword(changePasswordVerification: ChangePasswordVerification) {
        ifViewAttached { view ->
            if (validate(changePasswordVerification, view)) {
                view.showLoading()
                getToken().flatMap { token ->
                    changePasswordFragmentInteractor?.changePassword(
                        compositeDisposable,
                        changePasswordVerification, token
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
        }
    }

    private fun validate(
        changePasswordVerification: ChangePasswordVerification?,
        view: ChangePasswordFragmentView
    ): Boolean {
        if (!view.isNetworkAvailable()) {
            view.showMessageDialog(R.string.error_no_internet_connection)
            return false
        }
        if (changePasswordVerification?.oldPassword.isNullOrEmpty()) {
            view.showError(R.string.old_password_empty)
            return false
        }

        if (changePasswordVerification?.newPassword.isNullOrEmpty()) {
            view.showError(R.string.new_password_empty)
            return false
        }

        if (changePasswordVerification?.confirmPassword.isNullOrEmpty()) {
            view.showError(R.string.confirm_password_empty)
            return false
        }

        if (!changePasswordVerification?.confirmPassword.equals(changePasswordVerification?.newPassword)) {
            view.showError(R.string.password_and_confirm_password_must_be_same)
            return false
        }

        if (!isValidPassword(changePasswordVerification?.newPassword)) {
            view.showError(R.string.password_requirement)
            return false
        }

        if (!isValidPassword(changePasswordVerification?.confirmPassword)) {
            view.showError(R.string.password_requirement)
            return false
        }
        return true
    }

    private fun isValidPassword(password: String?): Boolean {
        val pattern: Pattern = Pattern.compile(StringConstants.validPassword)
        val matcher: Matcher = pattern.matcher(password.toString())
        return matcher.matches()
    }
}