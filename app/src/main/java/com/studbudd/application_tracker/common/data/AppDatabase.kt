package com.studbudd.application_tracker.common.data

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.studbudd.application_tracker.common.domain.ListConverterUseCase
import com.studbudd.application_tracker.feature_applications_management.data.dao.JobApplicationsDao
import com.studbudd.application_tracker.feature_applications_management.data.entity.LocalJobApplication
import com.studbudd.application_tracker.feature_user.data.dao.UserLocalDao
import com.studbudd.application_tracker.feature_user.data.models.local.UserEntity
import com.studbudd.application_tracker.utilities.DateConverter

@Database(entities = [LocalJobApplication::class, UserEntity::class], version = 3, exportSchema = false)
@TypeConverters(DateConverter::class, ListConverterUseCase::class)
abstract class AppDatabase : RoomDatabase() {

    abstract fun applicationsDao(): JobApplicationsDao
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