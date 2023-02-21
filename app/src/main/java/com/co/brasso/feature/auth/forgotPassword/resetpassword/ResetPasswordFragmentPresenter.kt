package com.co.brasso.feature.auth.forgotPassword.resetpassword

import android.util.Patterns
import com.co.brasso.R
import com.co.brasso.feature.shared.base.BasePresenter
import com.co.brasso.feature.shared.model.request.auth.ResetPassword
import com.co.brasso.utils.constant.StringConstants
import java.util.regex.Matcher
import java.util.regex.Pattern

class ResetPasswordFragmentPresenter : BasePresenter<ResetPasswordFragmentView>() {
    private var resetPasswordFragmentInteractor: ResetPasswordFragmentInteractor? = null

    override fun attachView(view: ResetPasswordFragmentView) {
        super.attachView(view)
        resetPasswordFragmentInteractor = ResetPasswordFragmentInteractor()
    }

    override fun detachView() {
        resetPasswordFragmentInteractor = null
        super.detachView()
    }

    fun resetPassword(resetPassword: ResetPassword?) {
        ifViewAttached { view ->
            if (view.isNetworkAvailable()) {
                if (validate(resetPassword, view)) {
                    view.showLoading()
                    resetPasswordFragmentInteractor?.resetPassword(
                        compositeDisposable,
                        resetPassword
                    )?.subscribe({
                        view.hideLoading()
                        view.success(it.message)
                    }, {
                        view.hideLoading()
                        showErrorView(it) {}
                    })?.let {
                        compositeDisposable?.add(it)
                    }
                }
            } else
                view.showMessageDialog(R.string.error_no_internet_connection)
        }
    }

    private fun validate(
        resetPassword: ResetPassword?,
        view: ResetPasswordFragmentView
    ): Boolean {
        if (resetPassword?.email.isNullOrEmpty()) {
            view.showMessageDialog(R.string.empty_email)
            return false
        } else if (!Patterns.EMAIL_ADDRESS.matcher(resetPassword?.email.toString()).matches()) {
            view.showMessageDialog(R.string.email_invalid)
            return false
        }
        if (resetPassword?.token.isNullOrEmpty()) {
            view.showMessageDialog(R.string.empty_otp)
            return false
        }
        if (resetPassword?.password.isNullOrEmpty()) {
            view.showMessageDialog(R.string.new_password_empty)
            return false
        }
        if (!isValidPassword(resetPassword?.password)) {
            view.showMessageDialog(R.string.password_requirement)
            return false
        }
        if (resetPassword?.confirmPassword.isNullOrEmpty()) {
            view.showMessageDialog(R.string.confirm_password_empty)
            return false
        }
        if (!isValidPassword(resetPassword?.confirmPassword)) {
            view.showMessageDialog(R.string.password_requirement)
            return false
        }
        if (!resetPassword?.confirmPassword.equals(resetPassword?.password)) {
            view.showMessageDialog(R.string.password_and_confirm_password_must_be_same)
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