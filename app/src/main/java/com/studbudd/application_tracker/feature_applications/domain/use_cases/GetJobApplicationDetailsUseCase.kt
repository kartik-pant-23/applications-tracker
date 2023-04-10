package com.studbudd.application_tracker.feature_applications.domain.use_cases

import com.studbudd.application_tracker.core.data.models.Resource
import com.studbudd.application_tracker.feature_applications.data.repo.JobApplicationsRepository

class GetJobApplicationDetailsUseCase(
    private val repo: JobApplicationsRepository
) {

    suspend operator fun invoke(id: Long)
        = repo.getApplicationDetails(id)

}