package com.studbudd.application_tracker.feature_applications.data.repo

import com.studbudd.application_tracker.core.data.models.Resource
import com.studbudd.application_tracker.feature_applications.data.models.remote.JobApplicationDto
import com.studbudd.application_tracker.feature_applications.domain.models.JobApplication

interface JobApplicationsRepository {

    suspend fun create(
        company: String,
        role: String,
        jobUrl: String,
        status: Int,
        notes: String?
    ): Resource<Unit>

}