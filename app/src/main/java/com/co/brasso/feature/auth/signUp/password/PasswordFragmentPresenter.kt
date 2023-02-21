package com.co.brasso.feature.auth.signUp.password

import com.co.brasso.R
import com.co.brasso.feature.auth.signUp.registration.RegistrationFragmentInteractor
import com.co.brasso.feature.shared.base.BasePresenter
import com.co.brasso.feature.shared.model.registration.EmailPasswordRegister
import com.co.brasso.utils.constant.StringConstants
import java.util.regex.Matcher
import java.util.regex.Pattern

class PasswordFragmentPresenter : BasePresenter<PasswordFragmentView>() {

    private var registrationFragmentInteractor: RegistrationFragmentInteractor? = null

    override fun attachView(view: PasswordFragmentView) {
        super.attachView(view)
        registrationFragmentInteractor = RegistrationFragmentInteractor()
    }

    override fun detachView() {
        registrationFragmentInteractor = null
        super.detachView()
    }

    fun verifyPassword(password: String?, email: String?) {
        ifViewAttached { view ->
            if (validate(password, view)) {
                passwordAndEmailRegistration(EmailPasswordRegister(email = email, password = password))
            }
        }
    }

    private fun validate(password: String?, view: PasswordFragmentView): Boolean {
        if (password.isNullOrEmpty()) {
            view.showMessageDialog(R.string.empty_password)
            return false
        }
        if (!isValidPassword(password)) {
            view.showMessageDialog(R.string.password_requirement)
            return false
        }
        return true
    }

    private fun isValidPassword(password: String?): Boolean {
        val pattern: Pattern = Pattern.compile(StringConstants.validPassword)
        val matcher: Matcher = pattern.matcher(password.toString())
        return matcher.matches()
    }

    private fun passwordAndEmailRegistration(userPasswordRegister: EmailPasswordRegister) {
        ifViewAttached { view ->
            if (view.isNetworkAvailable()) {
                view.showLoading()
                registrationFragmentInteractor?.userPasswordRegister(
                    compositeDisposable,
                    userPasswordRegister
                )?.subscribe({
                    view.hideLoading()
                    view.success(userPasswordRegister.password)
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