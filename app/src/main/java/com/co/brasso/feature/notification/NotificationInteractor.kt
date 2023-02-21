package com.co.brasso.feature.notification

import com.co.brasso.database.room.AppDatabase
import com.co.brasso.feature.shared.repositories.NotificationRepository
import io.reactivex.disposables.CompositeDisposable

class NotificationInteractor {
    fun getNotificationList(
        compositeDisposable: CompositeDisposable?,
        token: String) = NotificationRepository.getNotificationList(compositeDisposable,token)

    fun getAllNotifications(compositeDisposable: CompositeDisposable?, appDatabase: AppDatabase?) =
        NotificationRepository.getAllNotificationsFromDb(compositeDisposable, appDatabase)
}