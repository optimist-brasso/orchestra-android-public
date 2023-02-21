package com.co.brasso.feature.landing.myPage.tab.myPage.withdrawal

import com.co.brasso.feature.shared.model.withdraw.WithdrawEntity
import com.co.brasso.feature.shared.repositories.WithdrawFragmentRepository
import io.reactivex.disposables.CompositeDisposable

class WithdrawalFragmentInteractor {
    fun proceedToWithdraw(
        compositeDisposable: CompositeDisposable?,
        token: String,
        withdrawEntity: WithdrawEntity
    ) = WithdrawFragmentRepository.proceedToWithdraw(compositeDisposable, token, withdrawEntity)
}