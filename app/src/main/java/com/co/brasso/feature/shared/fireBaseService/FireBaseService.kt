package com.co.brasso.feature.shared.fireBaseService

import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import com.co.brasso.feature.splash.LoadingActivity
import com.co.brasso.utils.constant.BundleConstant
import com.co.brasso.utils.util.Logger
import com.co.brasso.utils.util.NotificationUtils
import com.co.brasso.utils.util.PreferenceUtils
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage

class FireBaseService: FirebaseMessagingService() {

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        NotificationManager.IMPORTANCE_HIGH
        val title = remoteMessage.notification?.title ?: ""
        val notificationBody = remoteMessage.notification?.body ?: ""
        Logger.d("jpNotificationService",remoteMessage.data.toString())
        val resultIntent = Intent(this, LoadingActivity::class.java)
        if (!remoteMessage.data.isNullOrEmpty()) {
            resultIntent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            resultIntent.action = Intent.ACTION_MAIN
            resultIntent.addCategory(Intent.CATEGORY_LAUNCHER)

            resultIntent.putExtra(
                BundleConstant.notificationValue,
                remoteMessage.data[BundleConstant.notificationValue]
            )
            resultIntent.putExtra(
                BundleConstant.notificationType,
                remoteMessage.data[BundleConstant.notificationType]
            )
            NotificationUtils.createNotification(
                this,
                title,
                notificationBody,
                remoteMessage.data[BundleConstant.notificationImage],
                resultIntent
            )
        } else {
            NotificationUtils.createNotification(
                this,
                title,
                notificationBody,
                "",
                resultIntent
            )
        }
    }

    override fun onNewToken(token: String) {
        super.onNewToken(token)
        PreferenceUtils.setFcmTokenRegistered(this,false)
    }
}