package com.co.brasso.feature.landing.myPage.tab.myPage.purchaseList

import com.co.brasso.feature.shared.repositories.PurchaseListRepository
import io.reactivex.disposables.CompositeDisposable

class PurchaseListFragmentInteractor {
    fun getPurchaseList(compositeDisposable: CompositeDisposable?, token: String?) =
        PurchaseListRepository.getPurchaseList(compositeDisposable, token)
}