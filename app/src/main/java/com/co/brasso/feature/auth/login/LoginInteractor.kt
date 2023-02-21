package com.co.brasso.feature.auth.login

import com.co.brasso.feature.shared.model.emailLogin.EmailLoginEntity
import com.co.brasso.feature.shared.model.request.auth.SocialLoginRequest
import com.co.brasso.feature.shared.repositories.AuthRepository
import com.co.brasso.feature.shared.repositories.EmailLoginRepository
import io.reactivex.disposables.CompositeDisposable

class LoginInteractor {

    fun emailLogin(compositeDisposable: CompositeDisposable?, emailLoginEntity: EmailLoginEntity) =
        EmailLoginRepository.emailLogin(compositeDisposable, emailLoginEntity)

    fun doSocialLogin(
        compositeDisposable: CompositeDisposable?,
        socialLoginRequest: SocialLoginRequest
    ) = AuthRepository.doSocialLogin(compositeDisposable,socialLoginRequest)
}