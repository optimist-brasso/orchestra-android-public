package com.co.brasso.feature.auth.signUpOptions

import com.co.brasso.R
import com.co.brasso.feature.shared.base.BasePresenter
import com.co.brasso.feature.shared.model.request.auth.SocialLoginRequest
import com.co.brasso.utils.constant.StringConstants

class SignUpOptionPresenter : BasePresenter<SignUpOptionView>() {

    private var signUpOptionInteractor: SignUpOptionInteractor? = null

    override fun attachView(view: SignUpOptionView) {
        super.attachView(view)
        signUpOptionInteractor = SignUpOptionInteractor()
    }

    override fun detachView() {
        signUpOptionInteractor = null
        super.detachView()
    }

    fun doSocialLogin(socialLoginRequest: SocialLoginRequest) {
        ifViewAttached { view ->
            if (view.isNetworkAvailable()) {
                view.showLoading()
                signUpOptionInteractor?.doSocialLogin(compositeDisposable, socialLoginRequest)
                    ?.subscribe({ login ->
                        view.hideLoading()
                        view.saveToken(login)
                        view.setLoginType(StringConstants.socialLogin)
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