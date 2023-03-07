package com.studbudd.application_tracker.feature_applications.data.repo

import androidx.annotation.WorkerThread
import com.studbudd.application_tracker.feature_applications.data.dao.JobApplicationsDao
import com.studbudd.application_tracker.feature_applications.data.models.local.JobApplicationEntity
import kotlinx.coroutines.flow.Flow
import java.util.*

class ApplicationsRepository(private val applicationsDao: JobApplicationsDao) {

    suspend fun getAllApplications(order_by_created: Boolean, latest_first: Boolean, status: List<Int>): List<JobApplicationEntity> {
        return if (order_by_created) {
            applicationsDao.getApplicationsByCreatedDate(latest_first, status)
        } else {
            applicationsDao.getApplicationsByModifiedDate(latest_first, status)
        }
    }
    fun getApplication(id: Int): Flow<JobApplicationEntity> = applicationsDao.getApplication(id)

    @WorkerThread
    suspend fun insertApplication(jobApplication: JobApplicationEntity): Long = applicationsDao.insert(jobApplication)!!

    @WorkerThread
    suspend fun updateApplication(jobApplication: JobApplicationEntity) {
        jobApplication.modifiedAtCalendar = Calendar.getInstance()
        applicationsDao.update(jobApplication)
    }

    @WorkerThread
    suspend fun deleteApplication(jobApplication: JobApplicationEntity) = applicationsDao.delete(jobApplication)

}