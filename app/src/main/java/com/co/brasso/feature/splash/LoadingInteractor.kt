package com.co.brasso.feature.splash

import com.android.billingclient.api.BillingClient
import com.android.billingclient.api.Purchase
import com.co.brasso.database.room.AppDatabase
import com.co.brasso.feature.shared.model.request.BoughtProducts
import com.co.brasso.feature.shared.model.response.appInfoAndNotification.AppInfoAndNotification
import com.co.brasso.feature.shared.model.response.appinfo.AppInfo
import com.co.brasso.feature.shared.model.response.notification.NotificationResponse
import com.co.brasso.feature.shared.repositories.LoadingRepository
import com.co.brasso.feature.shared.repositories.NotificationRepository
import com.co.brasso.feature.shared.repositories.PaymentVerifyRepository
import io.reactivex.Single
import io.reactivex.disposables.CompositeDisposable

class LoadingInteractor {

    fun getAppInfoAndNotifications(
        compositeDisposable: CompositeDisposable?,
        token: String?,
        billingClient: BillingClient?
    ): Single<AppInfoAndNotification> = Single.zip(
        LoadingRepository.getAppInfo(
            compositeDisposable
        ),
        NotificationRepository.getNotificationList(compositeDisposable, token),
        LoadingRepository.queryPurchaseAsync(billingClient),
        LoadingRepository.registerFcmToken(token,compositeDisposable)
    ) { appInfo: AppInfo, list: List<NotificationResponse>,purchases,successMessage ->
        AppInfoAndNotification(appInfo, list,purchases,successMessage)
    }

    fun saveNotificationsToDb(appDatabase: AppDatabase?, list: List<NotificationResponse>?) =
        NotificationRepository.saveNotificationsToDb(appDatabase, list)

    fun deleteNotifications(appDatabase: AppDatabase?) =
        NotificationRepository.deleteNotifications(appDatabase)

    fun getAllNotifications(compositeDisposable: CompositeDisposable?, appDatabase: AppDatabase?) =
        NotificationRepository.getAllNotificationsFromDb(compositeDisposable, appDatabase)

    fun purchaseVerify(
        compositeDisposable: CompositeDisposable?,
        boughtProducts: BoughtProducts?,
        token: String
    ) = PaymentVerifyRepository.purchaseVerify(compositeDisposable, boughtProducts, token)


    fun enableConsumableProduct(
        purchase: Purchase?,
        billingClient: BillingClient?
    ) = PaymentVerifyRepository.enableConsumableProduct(purchase,billingClient)


}