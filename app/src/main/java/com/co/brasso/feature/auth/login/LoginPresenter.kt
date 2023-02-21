package com.co.brasso.feature.auth.login

import android.util.Patterns
import com.co.brasso.R
import com.co.brasso.feature.shared.base.BasePresenter
import com.co.brasso.feature.shared.model.emailLogin.EmailLoginEntity
import com.co.brasso.feature.shared.model.request.auth.SocialLoginRequest
import com.co.brasso.utils.constant.StringConstants

class LoginPresenter : BasePresenter<LoginView>() {
    private var loginInteractor: LoginInteractor? = null

    override fun attachView(view: LoginView) {
        super.attachView(view)
        loginInteractor = LoginInteractor()
    }

    override fun detachView() {
        loginInteractor = null
        super.detachView()
    }

    fun emailLogin(emailLoginEntity: EmailLoginEntity) {
        ifViewAttached { view ->
            if (view.isNetworkAvailable()) {
                if (validate(emailLoginEntity, view)) {
                    view.showLoading()
                    loginInteractor?.emailLogin(compositeDisposable, emailLoginEntity)
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
                }
            } else
                view.showMessageDialog(R.string.error_no_internet_connection)
        }
    }

    private fun validate(emailLoginEntity: EmailLoginEntity?, view: LoginView): Boolean {
        if (emailLoginEntity?.email.isNullOrEmpty()) {
            view.showMessageDialog(R.string.empty_email)
            return false
        } else if (!Patterns.EMAIL_ADDRESS.matcher(emailLoginEntity?.email.toString()).matches()) {
            view.showMessageDialog(R.string.email_invalid)
            return false
        }
        if (emailLoginEntity?.password.isNullOrEmpty()) {
            view.showMessageDialog(R.string.empty_password)
            return false
        }
        return true
    }

    fun doSocialLogin(socialLoginRequest: SocialLoginRequest) {
        ifViewAttached { view ->
            if (view.isNetworkAvailable()) {
                view.showLoading()
                loginInteractor?.doSocialLogin(compositeDisposable, socialLoginRequest)
                    ?.subscribe({ login ->
                        view.hideLoading()
                        view.saveToken(login)
                        view.setLoginType(StringConstants.socialLogin)
                        view.navigateToMain()
                    }, {
                        view.hideLoading()
                        view.showMessage(it.localizedMessage ?: getDefaultErrorString())
                    })?.let {
                        compositeDisposable?.add(it)
                    }
            } else
                view.showMessageDialog(R.string.error_no_internet_connection)
        }
    }
}