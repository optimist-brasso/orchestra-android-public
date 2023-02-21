package com.co.brasso.feature.shared.repositories

import com.co.brasso.database.room.AppDatabase
import com.co.brasso.feature.shared.base.BaseRepository
import com.co.brasso.feature.shared.model.response.notification.NotificationResponse
import com.co.brasso.utils.extension.getSubscription
import io.reactivex.Completable
import io.reactivex.Single
import io.reactivex.disposables.CompositeDisposable

object NotificationRepository : BaseRepository() {

    fun getNotificationList(
        compositeDisposable: CompositeDisposable?,
        token: String?
    ): Single<List<NotificationResponse>> = Single.create { e ->
        apiService?.getNotification(token)
            .getSubscription()
            ?.subscribe({
                if (it.isSuccessful && it.body()?.data != null) {
                    e.onSuccess(it.body()?.data ?: listOf())
                } else {
                    e.onError(getError(it.code(), it.errorBody()?.string()))
                }
            }, {
                e.onError(getDefaultError(it))
            })?.let {
                compositeDisposable?.add(it)
            }
    }

    fun getNotificationDetail(
        compositeDisposable: CompositeDisposable?,
        token: String?,
        id: String?
    ): Single<NotificationResponse> = Single.create { e ->
        apiService?.getNotificationDetail(token, id)
            .getSubscription()
            ?.subscribe({
                if (it.isSuccessful && it.body()?.data != null) {
                    e.onSuccess(it.body()?.data!!)
                } else {
                    e.onError(getError(it.code(), it.errorBody()?.string()))
                }
            }, {
                e.onError(getDefaultError(it))
            })?.let {
                compositeDisposable?.add(it)
            }
    }

    fun saveNotificationsToDb(
        appDatabase: AppDatabase?,
        notificationResponse: List<NotificationResponse>?
    ): Single<List<NotificationResponse>?> =
        Completable.fromAction { appDatabase?.appDao()?.insertNotifications(notificationResponse) }
            .toSingleDefault(notificationResponse)

    fun deleteNotifications(
        appDatabase: AppDatabase?,
        ): Single<Boolean> =
        Completable.fromAction { appDatabase?.appDao()?.deleteNotification() }
            .toSingleDefault(true)

    fun getAllNotificationsFromDb(
        compositeDisposable: CompositeDisposable?,
        appDatabase: AppDatabase?
    ): Single<List<NotificationResponse>> =
        Single.create { e ->
            appDatabase?.appDao()?.getAllNotifications()?.getSubscription()
                ?.subscribe({
                    if (it != null)
                        e.onSuccess(it)
                    else {
                        closeDb(appDatabase)
                        e.onError(getDefaultError())
                    }
                }, {
                    closeDb(appDatabase)
                    e.onError(it)
                })?.let {
                    compositeDisposable?.add(it)
                }
        }

    fun updateNotificationsToDb(
        appDatabase: AppDatabase?,
        notificationResponse: NotificationResponse?
    ): Single<Boolean> =
        Completable.fromAction { appDatabase?.appDao()?.insertNotification(notificationResponse) }
            .toSingleDefault(true)
}