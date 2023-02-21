package com.co.brasso.database.room

import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.co.brasso.utils.constant.DbConstants

object Migrations {

    val migration1To2= object:Migration(1,2){
        override fun migrate(database: SupportSQLiteDatabase) {
            database.execSQL("ALTER TABLE ${DbConstants.fileDownload} " + " ADD COLUMN buttonType INTEGER")
        }
    }
}