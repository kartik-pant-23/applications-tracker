package com.studbudd.application_tracker.feature_applications_management.data.repo

import androidx.annotation.WorkerThread
import com.studbudd.application_tracker.feature_applications_management.data.dao.ApplicationsDao
import com.studbudd.application_tracker.feature_applications_management.data.entity.JobApplication
import kotlinx.coroutines.flow.Flow
import java.util.*

class ApplicationsRepository(private val applicationsDao: ApplicationsDao) {

    suspend fun getAllApplications(order_by_created: Boolean, latest_first: Boolean, status: List<Int>): List<JobApplication> {
        return if (order_by_created) {
            applicationsDao.getApplicationsByCreatedDate(latest_first, status)
        } else {
            applicationsDao.getApplicationsByModifiedDate(latest_first, status)
        }
    }
    fun getApplication(id: Int): Flow<JobApplication> = applicationsDao.getApplication(id)

    @WorkerThread
    suspend fun insertApplication(jobApplication: JobApplication): Long = applicationsDao.insert(jobApplication)

    @WorkerThread
    suspend fun updateApplication(jobApplication: JobApplication) {
        jobApplication.modifiedAt = Calendar.getInstance()
        applicationsDao.update(jobApplication)
    }

    @WorkerThread
    suspend fun deleteApplication(jobApplication: JobApplication) = applicationsDao.delete(jobApplication)

}