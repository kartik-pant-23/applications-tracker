package com.studbudd.application_tracker.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.studbudd.application_tracker.utilities.DATABASE_NAME
import com.studbudd.application_tracker.utilities.DateConverter

@Database(entities = [Application::class], version = 2, exportSchema = false)
@TypeConverters(DateConverter::class)
abstract class AppDatabase: RoomDatabase() {

    abstract fun applicationsDao(): ApplicationsDao

    companion object {
        @Volatile private var INSTANCE: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase {
            return INSTANCE ?: Room.databaseBuilder(
                context,
                AppDatabase::class.java,
                DATABASE_NAME
            ).build().also { INSTANCE = it }
        }

    }

}