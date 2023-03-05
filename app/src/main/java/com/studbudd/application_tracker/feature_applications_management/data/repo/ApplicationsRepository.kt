package com.studbudd.application_tracker.feature_applications_management.data.repo

import androidx.annotation.WorkerThread
import com.studbudd.application_tracker.feature_applications_management.data.dao.JobApplicationsDao
import com.studbudd.application_tracker.feature_applications_management.data.entity.LocalJobApplication
import kotlinx.coroutines.flow.Flow
import java.util.*

class ApplicationsRepository(private val applicationsDao: JobApplicationsDao) {

    suspend fun getAllApplications(order_by_created: Boolean, latest_first: Boolean, status: List<Int>): List<LocalJobApplication> {
        return if (order_by_created) {
            applicationsDao.getApplicationsByCreatedDate(latest_first, status)
        } else {
            applicationsDao.getApplicationsByModifiedDate(latest_first, status)
        }
    }
    fun getApplication(id: Int): Flow<LocalJobApplication> = applicationsDao.getApplication(id)

    @WorkerThread
    suspend fun insertApplication(jobApplication: LocalJobApplication): Long = applicationsDao.insert(jobApplication)!!

    @WorkerThread
    suspend fun updateApplication(jobApplication: LocalJobApplication) {
        jobApplication.modifiedAt = Calendar.getInstance()
        applicationsDao.update(jobApplication)
    }

    @WorkerThread
    suspend fun deleteApplication(jobApplication: LocalJobApplication) = applicationsDao.delete(jobApplication)

}