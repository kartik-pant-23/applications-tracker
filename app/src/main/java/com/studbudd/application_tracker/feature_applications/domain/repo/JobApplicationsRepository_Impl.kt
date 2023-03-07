package com.studbudd.application_tracker.feature_applications.domain.repo

import com.studbudd.application_tracker.core.data.models.Resource
import com.studbudd.application_tracker.core.domain.HandleApiCall
import com.studbudd.application_tracker.core.domain.HandleException
import com.studbudd.application_tracker.feature_applications.data.dao.JobApplicationsDao
import com.studbudd.application_tracker.feature_applications.data.dao.JobApplicationsApi
import com.studbudd.application_tracker.feature_applications.data.models.local.JobApplicationEntity
import com.studbudd.application_tracker.feature_applications.data.models.remote.JobApplicationDto
import com.studbudd.application_tracker.feature_applications.data.models.remote.requests.CreateRequest
import com.studbudd.application_tracker.feature_applications.data.repo.JobApplicationsRepository
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import javax.inject.Inject

class JobApplicationsRepository_Impl (
    private val dao: JobApplicationsDao,
    private val api: JobApplicationsApi,
    private val handleApiCall: HandleApiCall,
    private val isRemoteUser: Boolean
) : JobApplicationsRepository {

    private val handleException = HandleException()

    /**
     * This is the use-case that is responsible for adding new job
     * applications. It will create a local entry first, then try
     * to add this to remote database, if it [isRemoteUser]. On
     * successful creation of remote entity, the created job
     * application's `remote_id` gets updated with the `id` of the
     * remote job application.
     */
    override suspend fun create(
        company: String,
        role: String,
        jobUrl: String,
        status: Int,
        notes: String?
    ): Resource<Unit> {
        return try {
            val id = dao.insert(
                JobApplicationEntity(
                    companyName = company,
                    role = role,
                    jobLink = jobUrl,
                    status = status,
                    notes = notes
                )
            )

            return if (id != null && isRemoteUser) {
                GlobalScope.launch {
                    val reqParams = CreateRequest(
                        description = notes,
                        jobDetails = CreateRequest.JobDetails(
                            company = company,
                            role = role,
                            url = jobUrl
                        ),
                        status = status + 1 // TODO - fix when final implementation
                    )
                    createRemoteApplication(reqParams, id)
                }
                Resource.Success(Unit)
            } else {
                Resource.Failure("Oops.. Something went wrong!")
            }
        } catch (e: Exception) {
            handleException(TAG, e)
            Resource.Failure("Failed to insert application..")
        }
    }

    private suspend fun createRemoteApplication(
        reqParams: CreateRequest, applicationId: Long
    ) {
        val res = handleApiCall(
            apiCall = { api.createApplication(reqParams) },
            TAG = TAG
        )
        if (res is Resource.Success) {
            dao.updateRemoteId(id = applicationId, remoteId = res.data!!.id)
        }
    }

    companion object {
        const val TAG = "JobApplicationsRepository"
    }
}