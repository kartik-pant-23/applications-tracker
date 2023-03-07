package com.studbudd.application_tracker.feature_applications.domain.use_cases

import com.studbudd.application_tracker.core.data.models.Resource
import com.studbudd.application_tracker.feature_applications.data.repo.JobApplicationsRepository
import kotlinx.coroutines.*
import javax.inject.Inject

class CreateJobApplicationUseCase @Inject constructor(
    private val repo: JobApplicationsRepository,
    private val isRemoteUser: Boolean
) {

    /**
     * This is the use-case that is responsible for adding new job
     * applications. It will create a local entry first, then try
     * to add this to remote database, if it [isRemoteUser]. On
     * successful creation of remote entity, the created job
     * application's `remote_id` gets updated with the `id` of the
     * remote job application.
     */
    suspend operator fun invoke(
        company: String,
        role: String,
        url: String,
        description: String?,
        status: Int
    ): Resource<Boolean> = withContext(Dispatchers.IO) {
        val res = repo.createLocalApplication(
            company = company,
            role = role,
            jobUrl = url,
            description = description,
            status = status
        )

        if (res is Resource.Success) {
            if (isRemoteUser) {
                // if user has remote presence then adds remote application
                // in the **GlobalScope** without blocking any of the tasks at hand.
                GlobalScope.launch {
                    createRemoteApplication(
                        res.data!!, company, role, url, description, status
                    )
                }
            }
            Resource.Success(true)
        } else {
            Resource.Failure(res.message)
        }
    }

    /**
     * Add the job application to the remote database, and on successful
     * creation, update the local entity's `remote_id`.
     */
    private suspend fun createRemoteApplication(
        applicationId: Long,
        company: String,
        role: String,
        url: String,
        description: String?,
        status: Int
    ) {
        val res = repo.createRemoteApplication(
            company = company,
            role = role,
            jobUrl = url,
            description = description,
            status = status + 1 // In the remote database, status start from 1
        )

        if (res is Resource.Success) {
            val remoteId = res.data!!.id
            repo.updateRemoteIdOfLocalEntity(
                localApplicationId = applicationId,
                remoteId = remoteId
            )
        }
    }
}