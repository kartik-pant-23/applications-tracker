package com.studbudd.application_tracker.feature_applications.domain.repo

import com.studbudd.application_tracker.core.data.models.Resource
import com.studbudd.application_tracker.core.domain.HandleApiCall
import com.studbudd.application_tracker.core.domain.HandleException
import com.studbudd.application_tracker.feature_applications.data.dao.ApplicationStatusDao
import com.studbudd.application_tracker.feature_applications.data.dao.JobApplicationsDao
import com.studbudd.application_tracker.feature_applications.data.dao.JobApplicationsApi
import com.studbudd.application_tracker.feature_applications.data.models.local.JobApplicationEntity
import com.studbudd.application_tracker.feature_applications.data.models.local.JobApplicationWithStatus
import com.studbudd.application_tracker.feature_applications.data.models.remote.requests.CreateRequest
import com.studbudd.application_tracker.feature_applications.data.models.remote.requests.UpdateRequest
import com.studbudd.application_tracker.feature_applications.data.repo.JobApplicationsRepository
import com.studbudd.application_tracker.feature_applications.domain.models.ApplicationStatus
import com.studbudd.application_tracker.feature_applications.domain.models.JobApplication
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.flow

class JobApplicationsRepository_Impl(
    private val dao: JobApplicationsDao,
    private val applicationStatusDao: ApplicationStatusDao,
    private val api: JobApplicationsApi,
    private val handleApiCall: HandleApiCall,
    private val isRemoteUser: Boolean
) : JobApplicationsRepository {

    private val handleException = HandleException()

    /**
     * Tries to create a remote application, but does not bother if it fails,
     * unless it fails because of the reason of user being logged out, i.e.,
     * response is [Resource.LoggedOut]
     */
    override suspend fun create(
        company: String,
        role: String,
        jobUrl: String,
        status: Long,
        notes: String?
    ): Resource<JobApplication> {
        return try {
            val id = dao.insert(
                JobApplicationEntity(
                    company = company,
                    role = role,
                    jobLink = jobUrl,
                    status = status,
                    notes = notes
                )
            ) ?: return Resource.Failure("Oops.. Something went wrong!")

            val createdApplication = dao.getApplication(id).firstOrNull()
                ?: return Resource.Failure("Oops.. Something went wrong!")

            if (isRemoteUser) {
                return when (createRemoteApplication(data = createdApplication)) {
                    is Resource.Success -> Resource.Success(
                        data = createdApplication.toJobApplication(),
                        message = "Application created successfully"
                    )
                    is Resource.Failure -> Resource.Success(
                        data = createdApplication.toJobApplication(),
                        message = "Application created. Failed to sync with remote database."
                    )
                    is Resource.LoggedOut -> Resource.LoggedOut()
                }
            } else {
                return Resource.Success(createdApplication.toJobApplication())
            }
        } catch (e: Exception) {
            handleException(TAG, e)
            Resource.Failure("Failed to insert application..")
        }
    }

    private suspend fun createRemoteApplication(data: JobApplicationWithStatus): Resource<Boolean> {
        val reqParams = CreateRequest(
            description = data.application.notes,
            jobDetails = CreateRequest.JobDetails(
                company = data.application.company,
                role = data.application.role,
                url = data.application.jobLink
            ),
            status = data.status.id
        )
        return when (val res = handleApiCall(
            apiCall = { api.createApplication(reqParams) },
            TAG = TAG
        )) {
            is Resource.Success -> {
                dao.updateRemoteId(data.application.id, res.data!!.id)
                Resource.Success(true)
            }
            is Resource.Failure -> {
                Resource.Failure(res.message)
            }
            is Resource.LoggedOut -> Resource.LoggedOut()
        }
    }

    /**
     * Fetches a [Flow] of application details from the local database itself,
     * because local database is our single source of truth. Whatever sync needs
     * to happen, would happen in the starting of the app itself.
     */
    override suspend fun getApplications(pageSize: Int, pageNum: Int):
            Flow<Resource<List<JobApplication>>> = flow {
        try {
            dao.getApplications(pageSize, pageSize * (pageNum - 1)).collect {
                val applicationsList = it.map { item -> item.toJobApplication() }
                emit(Resource.Success(applicationsList))
            }
        } catch (e: Exception) {
            handleException(TAG, e)
            emit(Resource.Failure("Failed to fetch applications.."))
        }
    }

    override suspend fun getApplicationStatus(): Flow<Resource<List<ApplicationStatus>>> = flow {
        try {
            applicationStatusDao.getAllStatus().collect { data ->
                emit(Resource.Success(data.map { it.toApplicationStatus() }))
            }
        } catch (e: Exception) {
            handleException(TAG, e)
            emit(Resource.Failure("Oops.. Something went wrong!"))
        }
    }

    /**
     * Similar to [getApplications] function, this also fetches data only
     * from the local database, only thing being it returns [Flow] of
     * application's details.
     */
    override suspend fun getApplicationDetails(id: Long): Flow<Resource<JobApplication>> = flow {
        try {
            dao.getApplication(id).collect { data ->
                data?.let { emit(Resource.Success(it.toJobApplication())) }
                    ?: emit(Resource.Failure("No such application exists!"))
            }
        } catch (e: Exception) {
            handleException(TAG, e)
            emit(Resource.Failure("Oops.. Something went wrong!"))
        }
    }

    /**
     * Only allows to change the [status] and [notes] for the particular application.
     *
     * Steps to update the application's data -
     * 1. If application does not have remote presence then directly updating the
     * local application.
     * 2. Otherwise first update on the remote application, and on successful response,
     * update the local application as well.
     *
     * @param id Id of the application to update
     * @param status The new status of the application.
     * @param notes New status for the application, can be empty.
     *
     * @author Kartik Pant
     */
    override suspend fun updateApplication(
        id: Long,
        status: Long,
        notes: String?
    ): Resource<JobApplication> {
        return try {
            val resourceIsApplicationRemote = isApplicationRemote(id)
            if (resourceIsApplicationRemote is Resource.Success) {
                return if (resourceIsApplicationRemote.data != null) {
                    val remoteId = resourceIsApplicationRemote.data
                    return when (val resourceUpdateRemoteApplication = updateRemoteApplication(
                        localId = id,
                        remoteId = remoteId,
                        notes = notes,
                        status = status
                    )) {
                        is Resource.Success -> {
                            dao.update(id = id, status = status, notes = notes)
                            Resource.Success(resourceUpdateRemoteApplication.data!!)
                        }
                        is Resource.Failure -> {
                            resourceUpdateRemoteApplication
                        }
                        is Resource.LoggedOut -> {
                            Resource.LoggedOut()
                        }
                    }
                } else { // application does not have remote presence
                    dao.update(id = id, status = status, notes = notes)
                    val updatedApplication = dao.getApplication(id).firstOrNull()
                        ?: return Resource.Failure("Failed to fetch updated Application")
                    Resource.Success(updatedApplication.toJobApplication())
                }
            } else {
                Resource.Failure(resourceIsApplicationRemote.message)
            }
        } catch (e: Exception) {
            handleException(TAG, e)
            Resource.Failure("Failed to update the application. Something went wrong!")
        }
    }

    private suspend fun isApplicationRemote(id: Long): Resource<String?> {
        try {
            val application = dao.getApplication(id).firstOrNull()
                ?: return Resource.Failure("Application not present, try restarting the application!")
            return Resource.Success(application.application.remoteId)
        } catch (e: Exception) {
            throw e
        }
    }

    private suspend fun updateRemoteApplication(
        localId: Long,
        remoteId: String,
        notes: String?,
        status: Long
    ): Resource<JobApplication> {
        val requestBody = UpdateRequest(notes = notes, status = status)
        return when (val response = handleApiCall(
            apiCall = { api.updateApplication(remoteId = remoteId, body = requestBody) },
            TAG = TAG
        )) {
            is Resource.Success -> Resource.Success(response.data!!.toJobApplication(localId))
            is Resource.Failure -> Resource.Failure(response.message)
            is Resource.LoggedOut -> Resource.LoggedOut()
        }
    }

    companion object {
        const val TAG = "JobApplicationsRepository"
    }
}