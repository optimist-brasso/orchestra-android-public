package com.co.brasso.feature.landing.myPage.tab.myPage.points

import com.android.billingclient.api.BillingClient
import com.android.billingclient.api.Purchase
import com.co.brasso.feature.shared.model.request.BoughtProducts
import com.co.brasso.feature.shared.model.response.GetBundleInfo
import com.co.brasso.feature.shared.repositories.BundleListRepository
import com.co.brasso.feature.shared.repositories.CartListRepository
import com.co.brasso.feature.shared.repositories.MyProfileFragmentRepository
import com.co.brasso.feature.shared.repositories.PaymentVerifyRepository
import io.reactivex.Single
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.functions.BiFunction

class PointsListInteractor {
    fun getBundleList(
        compositeDisposable: CompositeDisposable?,
        token: String
    ): Single<GetBundleInfo> = Single.zip(
        MyProfileFragmentRepository.getProfile(compositeDisposable, token),
        BundleListRepository.getBundleList(compositeDisposable, token),
        BiFunction { success, products ->
            GetBundleInfo(success, products)
        }
    )

    fun purchaseVerify(
        compositeDisposable: CompositeDisposable?,
        boughtProducts: BoughtProducts?,
        token: String
    ) = PaymentVerifyRepository.purchaseVerify(compositeDisposable, boughtProducts, token)

    fun checkProductAvailability(
        bundleIdentifier: String?,
        billingClient: BillingClient?
    ) = CartListRepository.checkProductAvailability(
        bundleIdentifier,
        billingClient
    )

    fun enableConsumableProduct(
        purchase: Purchase?,
        billingClient: BillingClient?
    ) = PaymentVerifyRepository.enableConsumableProduct(purchase,billingClient)

}