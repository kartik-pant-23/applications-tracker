package com.studbudd.application_tracker.data

import androidx.annotation.WorkerThread
import kotlinx.coroutines.flow.Flow
import java.util.*

class ApplicationsRepository(private val applicationsDao: ApplicationsDao) {

    suspend fun getAllApplications(order_by_created: Boolean, latest_first: Boolean, status: List<Int>): List<Application> {
        return if (order_by_created) {
            applicationsDao.getApplicationsByCreatedDate(latest_first, status)
        } else {
            applicationsDao.getApplicationsByModifiedDate(latest_first, status)
        }
    }
    fun getApplication(id: Int): Flow<Application> = applicationsDao.getApplication(id)

    @WorkerThread
    suspend fun insertApplication(application: Application): Long = applicationsDao.insert(application)

    @WorkerThread
    suspend fun updateApplication(application: Application) {
        application.modified_at = Calendar.getInstance()
        applicationsDao.update(application)
    }

    @WorkerThread
    suspend fun deleteApplication(application: Application) = applicationsDao.delete(application)

}