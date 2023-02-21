package com.co.brasso.feature.landing.home

import com.co.brasso.database.room.AppDatabase
import com.co.brasso.feature.shared.base.BaseInteractor
import com.co.brasso.feature.shared.model.response.BannerAndNotification
import com.co.brasso.feature.shared.model.response.notification.NotificationResponse
import com.co.brasso.feature.shared.repositories.HomeRepository
import com.co.brasso.feature.shared.repositories.NotificationRepository
import io.reactivex.Single
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.functions.BiFunction

class HomeInteractor : BaseInteractor() {
    fun getBanner(
        compositeDisposable: CompositeDisposable?,
        token: String
    ): Single<BannerAndNotification> =
        Single.zip(HomeRepository.getBanner(compositeDisposable, token),
            NotificationRepository.getNotificationList(compositeDisposable, token),
            BiFunction { home, notification ->
                BannerAndNotification(home, notification)
            })

    fun getAllNotifications(compositeDisposable: CompositeDisposable?, appDatabase: AppDatabase?) =
        NotificationRepository.getAllNotificationsFromDb(compositeDisposable, appDatabase)

    fun saveNotificationsToDb(appDatabase: AppDatabase?, list: List<NotificationResponse>?) =
        NotificationRepository.saveNotificationsToDb(appDatabase, list)

    fun deleteNotifications(appDatabase: AppDatabase?) =
        NotificationRepository.deleteNotifications(appDatabase)

}