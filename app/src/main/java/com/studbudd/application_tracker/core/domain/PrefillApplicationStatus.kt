package com.studbudd.application_tracker.core.domain

import android.database.sqlite.SQLiteDatabase
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.studbudd.application_tracker.core.data.AppDatabase

class PrefillApplicationStatus: RoomDatabase.Callback() {
    override fun onCreate(db: SupportSQLiteDatabase) {
        val applicationStatusList = AppDatabase.defaultApplicationStatus
        for (item in applicationStatusList) {
            db.insert("application_status", SQLiteDatabase.CONFLICT_REPLACE, item)
        }
    }
}