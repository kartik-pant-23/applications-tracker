package com.studbudd.application_tracker

import android.app.Application
import com.studbudd.application_tracker.data.AppDatabase
import com.studbudd.application_tracker.data.ApplicationsRepository

class ApplicationsStart: Application() {

    val database by lazy { AppDatabase.getInstance(this) }
    val repository by lazy { ApplicationsRepository(database.applicationsDao()) }

}