package com.studbudd.application_tracker.common.data

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.studbudd.application_tracker.common.domain.ListConverterUseCase
import com.studbudd.application_tracker.feature_applications_management.data.dao.ApplicationsDao
import com.studbudd.application_tracker.feature_applications_management.data.entity.JobApplication
import com.studbudd.application_tracker.feature_user.data.dao.UserLocalDao
import com.studbudd.application_tracker.feature_user.data.entity.UserLocal
import com.studbudd.application_tracker.utilities.DateConverter

@Database(entities = [JobApplication::class, UserLocal::class], version = 3, exportSchema = false)
@TypeConverters(DateConverter::class, ListConverterUseCase::class)
abstract class AppDatabase : RoomDatabase() {

    abstract fun applicationsDao(): ApplicationsDao
    abstract fun userLocalDao(): UserLocalDao

    companion object {
        val Migration_2_3 = object : Migration(2, 3) {
            override fun migrate(database: SupportSQLiteDatabase) {
                database.execSQL(
                    "CREATE TABLE `users` " +
                            "(`id` INTEGER NOT NULL, " +
                            "`name` TEXT NOT NULL, " +
                            "`email` TEXT, " +
                            "`remoteId` TEXT, " +
                            "`photoUrl` TEXT, " +
                            "`createdAt` TEXT, " +
                            "`placeholderKeys` TEXT NOT NULL, " +
                            "`placeholderValues` TEXT NOT NULL, " +
                            "PRIMARY KEY(`id`))"
                )
                database.execSQL(
                    "ALTER TABLE `applications`" +
                            "ADD COLUMN `remote_id` TEXT"
                )
            }
        }
    }

}