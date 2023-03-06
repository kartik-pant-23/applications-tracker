package com.studbudd.application_tracker.feature_applications_management.domain.repo

import com.studbudd.application_tracker.common.data.models.Resource
import com.studbudd.application_tracker.common.domain.HandleApiCall
import com.studbudd.application_tracker.common.domain.HandleException
import com.studbudd.application_tracker.feature_applications_management.data.dao.JobApplicationsDao
import com.studbudd.application_tracker.feature_applications_management.data.dao.JobApplicationsRemoteDao
import com.studbudd.application_tracker.feature_applications_management.data.entity.LocalJobApplication
import com.studbudd.application_tracker.feature_applications_management.data.entity.RemoteJobApplication
import com.studbudd.application_tracker.feature_applications_management.data.entity.requests.CreateRequest
import com.studbudd.application_tracker.feature_applications_management.data.repo.JobApplicationsRepository
import javax.inject.Inject

class JobApplicationsRepository_Impl @Inject constructor(
    private val localDao: JobApplicationsDao,
    private val remoteDao: JobApplicationsRemoteDao,
    private val handleApiCall: HandleApiCall
) : JobApplicationsRepository {

    private val handleException = HandleException()

    override suspend fun createLocalApplication(
        company: String,
        role: String,
        jobUrl: String,
        status: Int,
        description: String?
    ): Resource<Long> {
        return try {
            val id = localDao.insert(
                LocalJobApplication(
                    companyName = company,
                    role = role,
                    jobLink = jobUrl,
                    status = status,
                    notes = description
                )
            )
            if (id != null)
                Resource.Success(id, "Application created!")
            else
                Resource.Failure("Something went wrong")
        } catch (e: Exception) {
            handleException(TAG, e)
            Resource.Failure("Something went wrong")
        }
    }

    override suspend fun updateRemoteIdOfLocalEntity(
        localApplicationId: Long,
        remoteId: String
    ) {
        try {
            localDao.updateRemoteId(localApplicationId, remoteId)
        } catch (e: Exception) {
            handleException(TAG, e)
        }
    }

    override suspend fun createRemoteApplication(
        company: String,
        role: String,
        jobUrl: String,
        status: Int,
        description: String?
    ): Resource<RemoteJobApplication> {
        val requestParams = CreateRequest(
            description = description,
            jobDetails = CreateRequest.JobDetails(
                company = company,
                role = role,
                url = jobUrl
            ),
            status = status
        )
        return handleApiCall(
            apiCall = { remoteDao.createApplication(requestParams) },
            TAG = TAG
        )
    }

    companion object {
        const val TAG = "JobApplicationsRepository"
    }
}