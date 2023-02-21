package com.co.brasso.feature.landing.myPage.tab.myPage.billingHistory

import com.co.brasso.feature.shared.base.BaseInteractor
import com.co.brasso.feature.shared.repositories.BillingHistoryRepository
import io.reactivex.disposables.CompositeDisposable

class BillingHistoryInteractor : BaseInteractor() {

    fun getBillingHistory(compositeDisposable: CompositeDisposable?, token: String) =
        BillingHistoryRepository.getBillingHistory(compositeDisposable,token)
}