package com.co.brasso.database.room

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.co.brasso.feature.download.FileDownload
import com.co.brasso.feature.shared.model.response.notification.NotificationResponse
import com.co.brasso.utils.constant.DbConstants

@Database(
    entities = [NotificationResponse::class,FileDownload::class],
    version = 2
)

abstract class AppDatabase : RoomDatabase() {
    abstract fun appDao(): AppDao

    companion object {
        private var appDatabase: AppDatabase? = null

        fun getAppDatabase(context: Context?) =
            if (appDatabase != null && appDatabase?.isOpen == true) {
                appDatabase
            } else {
                if (context != null)
                    Room.databaseBuilder(context, AppDatabase::class.java, DbConstants.dbName).addMigrations(Migrations.migration1To2)
                        .build()
                else
                    null
            }
    }
}
