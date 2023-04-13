package com.studbudd.application_tracker.feature_applications.domain.use_cases

import com.studbudd.application_tracker.core.data.models.Resource
import com.studbudd.application_tracker.feature_applications.data.repo.JobApplicationsRepository

class DeleteJobApplicationUseCase(
    private val repo: JobApplicationsRepository,
    private val handlePeriodicNotification: HandlePeriodicNotification
    ) {
    suspend operator fun invoke(id: Long): Resource<Unit> {
        return when (val result = repo.deleteApplication(id)) {
            is Resource.Success -> handlePeriodicNotification.delete(result.data!!)
            is Resource.Failure -> Resource.Failure(result.message)
            is Resource.LoggedOut -> Resource.LoggedOut()
        }
    }
}