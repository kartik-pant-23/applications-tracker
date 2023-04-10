package com.studbudd.application_tracker.feature_applications.data.repo

import androidx.annotation.WorkerThread
import com.studbudd.application_tracker.feature_applications.data.dao.JobApplicationsDao
import com.studbudd.application_tracker.feature_applications.data.models.local.JobApplicationEntity_Old
import com.studbudd.application_tracker.feature_applications.data.models.local.JobApplicationWithStatus
import kotlinx.coroutines.flow.Flow

class ApplicationsRepository(private val applicationsDao: JobApplicationsDao) {

    fun getApplication(id: Int) = applicationsDao.getApplication(id.toLong())

    @WorkerThread
    suspend fun updateApplication(jobApplication: JobApplicationEntity_Old) {
        applicationsDao.update(jobApplication)
    }

    @WorkerThread
    suspend fun deleteApplication(jobApplication: JobApplicationEntity_Old) = applicationsDao.delete(jobApplication)

}