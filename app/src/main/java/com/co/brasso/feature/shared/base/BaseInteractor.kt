package com.co.brasso.feature.shared.base

import com.co.brasso.feature.shared.model.emailLogin.EmailLoginEntity
import com.co.brasso.feature.shared.repositories.EmailLoginRepository
import io.reactivex.disposables.CompositeDisposable

open class BaseInteractor {
    fun doRefreshToken(compositeDisposable : CompositeDisposable?, refreshRequest: EmailLoginEntity) =
        EmailLoginRepository.emailLogin(compositeDisposable,refreshRequest)

}