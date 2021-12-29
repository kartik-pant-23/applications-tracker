package com.studbudd.application_tracker.data

import androidx.annotation.WorkerThread
import kotlinx.coroutines.flow.Flow

class ApplicationsRepository(private val applicationsDao: ApplicationsDao) {

    val applicationsList: Flow<List<Application>> = applicationsDao.getAllApplications()
    fun getApplication(id: Int): Flow<Application> = applicationsDao.getApplication(id)

    @WorkerThread
    suspend fun insertApplication(application: Application) = applicationsDao.insert(application)

    @WorkerThread
    suspend fun deleteApplication(application: Application) = applicationsDao.delete(application)

}