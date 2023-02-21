package com.co.brasso.feature.notification.notificationDetails

import com.co.brasso.database.room.AppDatabase
import com.co.brasso.feature.shared.model.response.notification.NotificationResponse
import com.co.brasso.feature.shared.repositories.NotificationRepository
import io.reactivex.disposables.CompositeDisposable

class NotificationDetailInteractor {
    fun getNotificationDetail(
        compositeDisposable: CompositeDisposable?,
        token: String, id: String?
    ) = NotificationRepository.getNotificationDetail(compositeDisposable, token, id)

    fun updateNotificationsToDb(appDatabase: AppDatabase?, notificationResponse: NotificationResponse?) =
        NotificationRepository.updateNotificationsToDb(appDatabase, notificationResponse)
}