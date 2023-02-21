package com.co.brasso.feature.auth.signUp.registration

import com.co.brasso.feature.shared.model.emailLogin.EmailLoginEntity
import com.co.brasso.feature.shared.model.registration.EmailPasswordRegister
import com.co.brasso.feature.shared.model.registration.UserRegistrationEntity
import com.co.brasso.feature.shared.repositories.EmailLoginRepository
import com.co.brasso.feature.shared.repositories.RegistrationRepository
import io.reactivex.disposables.CompositeDisposable

class RegistrationFragmentInteractor {

    fun register(
        compositeDisposable: CompositeDisposable?,
        userRegistrationEntity: UserRegistrationEntity,
        token:String?
    ) = RegistrationRepository.register(compositeDisposable,userRegistrationEntity,token)

    fun userPasswordRegister(
        compositeDisposable: CompositeDisposable?,
        userRegistrationEntity: EmailPasswordRegister
    ) = RegistrationRepository.emailPasswordRegister(compositeDisposable,userRegistrationEntity)

    fun emailLogin(compositeDisposable: CompositeDisposable?, emailLoginEntity: EmailLoginEntity) =
        EmailLoginRepository.emailLogin(compositeDisposable, emailLoginEntity)

}