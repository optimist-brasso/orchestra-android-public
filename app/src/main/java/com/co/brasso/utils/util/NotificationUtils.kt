package com.co.brasso.utils.util

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import com.co.brasso.R
import java.io.IOException
import java.net.HttpURLConnection
import java.net.URL

object NotificationUtils {
    fun createNotification(
        context: Context?, title: String?, message: String?, imageUrl: String?, intent: Intent?
    ) {
        if (context == null)
            return
        val notificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as? NotificationManager
        val channelId =
            context.getString(R.string.app_name) + System.currentTimeMillis().toInt().toString()
        val builder: NotificationCompat.Builder
        val pendingIntent =
            PendingIntent.getActivity(context, 0, intent,   PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            var channel = notificationManager?.getNotificationChannel(channelId)
            if (channel == null) {
                channel = NotificationChannel(
                    channelId,
                    context.getString(R.string.app_name),
                    NotificationManager.IMPORTANCE_HIGH
                )
                channel.description = message
                channel.enableVibration(true)
                channel.lightColor = ContextCompat.getColor(context, R.color.colorPrimary)
                notificationManager?.createNotificationChannel(channel)
            }
            builder = NotificationCompat.Builder(context, channelId)
            builder.setContentTitle(title)
                .setContentText(message)
                .setDefaults(Notification.DEFAULT_ALL)
                .setAutoCancel(true)
                .setContentIntent(pendingIntent)
                .setTicker(message)
                .setLargeIcon(BitmapFactory.decodeResource(context.resources, R.mipmap.ic_launcher))
                .setStyle(NotificationCompat.BigTextStyle().bigText(message))
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
        } else {
            builder = NotificationCompat.Builder(context)
            builder.setContentTitle(title)
                .setContentText(message)
                .setDefaults(Notification.DEFAULT_ALL)
                .setAutoCancel(true)
                .setContentIntent(pendingIntent)
                .setTicker(message)
                .setLargeIcon(BitmapFactory.decodeResource(context.resources, R.mipmap.ic_launcher))
                .setStyle(NotificationCompat.BigTextStyle().bigText(message))
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
        }
        builder.setSmallIcon(R.drawable.ic_notification_icon_transparent)
        builder.color = ContextCompat.getColor(context, R.color.colorAccent)
        if (!imageUrl.isNullOrBlank()) {
            val bitmap = getBitmapFromURL(imageUrl)
            if (bitmap != null) {
                addBigImageStyle(builder, title, message, bitmap)
            }
        }
        notificationManager?.notify(System.currentTimeMillis().toInt(), builder.build())
    }

    private fun addBigImageStyle(
        builder: NotificationCompat.Builder, title: String?, message: String?, bitmap: Bitmap
    ) {
        val bigPictureNotificationStyle = NotificationCompat.BigPictureStyle()
            .setBigContentTitle(title)
            .setSummaryText(message)
            .bigPicture(bitmap)
        builder.setStyle(bigPictureNotificationStyle)
    }

    /**
     * Downloading push notification image before displaying it in
     * the notification tray
     */
    private fun getBitmapFromURL(strURL: String?) =
        try {
            val url = URL(strURL)
            val connection = url.openConnection() as? HttpURLConnection
            connection?.doInput = true
            connection?.connect()
            BitmapFactory.decodeStream(connection?.inputStream)
        } catch (e: IOException) {
            e.printStackTrace()
            null
        }

}