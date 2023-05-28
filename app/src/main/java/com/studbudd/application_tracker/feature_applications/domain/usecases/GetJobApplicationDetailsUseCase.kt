package com.studbudd.application_tracker.feature_applications.domain.usecases

import com.studbudd.application_tracker.feature_applications.data.repo.JobApplicationsRepository

class GetJobApplicationDetailsUseCase(
    private val repo: JobApplicationsRepository
) {

    suspend operator fun invoke(id: Long)
        = repo.getApplicationDetails(id)

}