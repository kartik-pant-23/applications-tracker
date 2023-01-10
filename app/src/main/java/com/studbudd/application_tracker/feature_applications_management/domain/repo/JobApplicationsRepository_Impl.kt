package com.studbudd.application_tracker.feature_applications_management.domain.repo

import com.studbudd.application_tracker.common.data.models.Resource
import com.studbudd.application_tracker.common.domain.HandleException
import com.studbudd.application_tracker.feature_applications_management.data.dao.JobApplicationsDao
import com.studbudd.application_tracker.feature_applications_management.data.entity.JobApplication
import com.studbudd.application_tracker.feature_applications_management.data.repo.JobApplicationsRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class JobApplicationsRepository_Impl @Inject constructor(
    private val daoLocal: JobApplicationsDao
) : JobApplicationsRepository {

    private val handleException = HandleException()

    override suspend fun addJobApplication(application: JobApplication): Resource<Long> {
        return try {
            val id = daoLocal.insert(application)
            if (id != null) Resource.Success(id)
            else Resource.Failure("Something went wrong!")
        } catch (e: Exception) {
            handleException(TAG, e)
            Resource.Failure("Something went wrong!")
        }
    }

    override fun getJobApplications(): Flow<List<JobApplication>> {
        TODO("Not yet implemented")
    }

    override fun getJobApplicationById(id: Int): Flow<JobApplication> {
        TODO("Not yet implemented")
    }

    override suspend fun updateJobApplication(newApplication: JobApplication) {
        TODO("Not yet implemented")
    }

    override fun deleteJobApplicationById(id: Int) {
        TODO("Not yet implemented")
    }

    companion object {
        val TAG = "JobApplicationsRepository"
    }
}