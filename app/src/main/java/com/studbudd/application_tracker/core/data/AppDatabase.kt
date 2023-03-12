package com.studbudd.application_tracker.core.data

import android.content.ContentValues
import android.database.sqlite.SQLiteDatabase
import androidx.core.content.contentValuesOf
import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.room.migration.Migration
import androidx.sqlite.db.SimpleSQLiteQuery
import androidx.sqlite.db.SupportSQLiteDatabase
import androidx.sqlite.db.SupportSQLiteQueryBuilder
import com.studbudd.application_tracker.core.domain.ListConverterUseCase
import com.studbudd.application_tracker.feature_applications.data.dao.JobApplicationsDao
import com.studbudd.application_tracker.feature_applications.data.models.local.JobApplicationEntity_Old
import com.studbudd.application_tracker.feature_user.data.dao.UserDao
import com.studbudd.application_tracker.feature_user.data.models.local.UserEntity
import com.studbudd.application_tracker.core.utils.DateConverter
import com.studbudd.application_tracker.core.utils.TimestampHelper
import com.studbudd.application_tracker.feature_applications.data.models.local.ApplicationStatusEntity
import com.studbudd.application_tracker.feature_applications.data.models.local.JobApplicationEntity
import java.util.Calendar
import java.util.Date

@Database(
    entities = [
        JobApplicationEntity::class,
        ApplicationStatusEntity::class,
        JobApplicationEntity_Old::class,
        UserEntity::class
    ],
    version = AppDatabase.DB_VERSION,
    exportSchema = false
)
@TypeConverters(
    DateConverter::class,
    ListConverterUseCase::class
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun applicationsDao(): JobApplicationsDao
    abstract fun userLocalDao(): UserDao

    companion object {
        const val DB_VERSION = 5

        // Adding users table
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

        // Adding companyLogo, and applicationDeadline in the table
        val Migration_3_4 = object : Migration(3, 4) {
            override fun migrate(database: SupportSQLiteDatabase) {
                database.execSQL("ALTER TABLE `applications` ADD COLUMN `company_logo` TEXT")
                database.execSQL("ALTER TABLE `applications` ADD COLUMN `application_deadline` TEXT")
            }
        }

        // Creating application status table with some data
        val Migration_4_5 = object : Migration(4, 5) {
            override fun migrate(database: SupportSQLiteDatabase) {
                database.execSQL(
                    "CREATE TABLE `application_status` (" +
                            "`id` INTEGER PRIMARY KEY NOT NULL, " +
                            "`tag` TEXT NOT NULL, " +
                            "`color` TEXT NOT NULL, " +
                            "`colorNight` TEXT NOT NULL, " +
                            "`importanceValue` INT NOT NULL" +
                            ")"
                )

                for (status in defaultApplicationStatus) {
                    database.insert(
                        "application_status",
                        SQLiteDatabase.CONFLICT_REPLACE,
                        status
                    )
                }

                database.execSQL("ALTER TABLE `applications` RENAME TO `applications_old`")

                database.execSQL("CREATE TABLE `applications` (" +
                        "`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, " +
                        "`remoteId` TEXT, " +
                        "`company` TEXT NOT NULL, " +
                        "`companyLogo` TEXT, " +
                        "`role` TEXT NOT NULL, " +
                        "`jobLink` TEXT NOT NULL, " +
                        "`notes` TEXT, " +
                        "`status` INTEGER NOT NULL, " +
                        "`applicationDeadline` TEXT, " +
                        "`createdAt` TEXT NOT NULL, " +
                        "`modifiedAt` TEXT NOT NULL," +
                        "FOREIGN KEY (`status`) REFERENCES `application_status`(`id`) ON UPDATE CASCADE ON DELETE SET NULL" +
                        ")")

                val cursor = database.query(SimpleSQLiteQuery("SELECT * from applications_old"))
                val idIndex = cursor.getColumnIndex("application_id")
                val remoteIdIndex = cursor.getColumnIndex("remote_id")
                val companyIndex = cursor.getColumnIndex("company_name")
                val companyLogoIndex = cursor.getColumnIndex("company_logo")
                val roleIndex = cursor.getColumnIndex("role")
                val jobLinkIndex = cursor.getColumnIndex("jobLink")
                val notesIndex = cursor.getColumnIndex("notes")
                val statusIndex = cursor.getColumnIndex("status")
                val applicationDeadlineIndex = cursor.getColumnIndex("application_deadline")
                val createdAtIndex = cursor.getColumnIndex("created_at")
                val modifiedAtIndex = cursor.getColumnIndex("modified_at")
                cursor.moveToNext()
                while (!cursor.isAfterLast) {
                    val createdAt = TimestampHelper.getDateString(
                        Calendar.getInstance().apply { timeInMillis = cursor.getLong(createdAtIndex) }.time
                    ) ?: TimestampHelper.getCurrentTimestamp()
                    val modifiedAt = TimestampHelper.getDateString(
                        Calendar.getInstance().apply { timeInMillis = cursor.getLong(modifiedAtIndex) }.time
                    ) ?: TimestampHelper.getCurrentTimestamp()
                    database.insert(
                        "applications",
                        SQLiteDatabase.CONFLICT_REPLACE,
                        contentValuesOf(
                            Pair("id", cursor.getLong(idIndex)),
                            Pair("remoteId", cursor.getString(remoteIdIndex)),
                            Pair("company", cursor.getString(companyIndex)),
                            Pair("companyLogo", cursor.getString(companyLogoIndex)),
                            Pair("role", cursor.getString(roleIndex)),
                            Pair("jobLink", cursor.getString(jobLinkIndex)),
                            Pair("notes", cursor.getString(notesIndex)),
                            Pair("status", cursor.getLong(statusIndex)),
                            Pair("applicationDeadline", cursor.getString(applicationDeadlineIndex)),
                            Pair("createdAt", createdAt),
                            Pair("modifiedAt", modifiedAt)
                        )
                    )
                    cursor.moveToNext()
                }

//                database.execSQL("DROP TABLE `applications_old`") // TODO - uncomment this once old entity is not used anymore
            }
        }

        val defaultApplicationStatus = listOf(
            contentValuesOf(
                Pair("id", 0),
                Pair("tag", "waiting for referral"),
                Pair("color", "#D0A200"),
                Pair("colorNight", "#FFC700"),
                Pair("importanceValue", 50)
            ),
            contentValuesOf(
                Pair("id", 1),
                Pair("tag", "applied"),
                Pair("color", "#006CBA"),
                Pair("colorNight", "#44B0FF"),
                Pair("importanceValue", 40)
            ),
            contentValuesOf(
                Pair("id", 2),
                Pair("tag", "applied with referral"),
                Pair("color", "#C100AE"),
                Pair("colorNight", "#FF8CF4"),
                Pair("importanceValue", 30)
            ),
            contentValuesOf(
                Pair("id", 3),
                Pair("tag", "rejected"),
                Pair("color", "#C90000"),
                Pair("colorNight", "#FF5A5A"),
                Pair("importanceValue", 20)
            ),
            contentValuesOf(
                Pair("id", 4),
                Pair("tag", "selected"),
                Pair("color", "#00A811"),
                Pair("colorNight", "#49FF5B"),
                Pair("importanceValue", 10)
            ),
        )
    }

}