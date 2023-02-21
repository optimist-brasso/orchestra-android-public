package com.co.brasso.feature.auth.signUpOptions

import com.co.brasso.feature.shared.model.request.auth.SocialLoginRequest
import com.co.brasso.feature.shared.repositories.AuthRepository
import io.reactivex.disposables.CompositeDisposable

class SignUpOptionInteractor {

    fun doSocialLogin(
        compositeDisposable: CompositeDisposable?,
        socialLoginRequest: SocialLoginRequest
    ) = AuthRepository.doSocialLogin(compositeDisposable,socialLoginRequest)
}