package com.studbudd.application_tracker.feature_applications_management.domain.use_cases

import com.studbudd.application_tracker.common.data.models.Resource
import com.studbudd.application_tracker.feature_applications_management.data.entity.JobApplication
import com.studbudd.application_tracker.feature_applications_management.data.repo.ApplicationsRepository
import javax.inject.Inject

/**
 * Adds new job application for the user. This function is available even if the
 * user is not having active internet connection.
 *
 * So for this reason the process flow is as follows -
 * 1. First add the application into the local database.
 * 2. If the user, is a remote user (has token), then add this application to the remote database.
 * 3. On successful creation of the remote entity add the `id` to `remote_id` of local application.
 */
class CreateApplicationUseCase @Inject constructor(
    private val repo: ApplicationsRepository,
    private val isRemoteUser: Boolean = false
) {

    suspend operator fun invoke(company: String, role: String, url: String, notes: String?, status: Int) {
        // add this application to local database
        repo.insertApplication(JobApplication(
            companyName = company,
            role = role,
            jobLink = url,
            notes = notes,
            status = status
        ))

        if (isRemoteUser) {

        }
    }

}