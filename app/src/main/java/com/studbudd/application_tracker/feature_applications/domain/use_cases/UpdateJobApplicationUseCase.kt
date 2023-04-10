package com.studbudd.application_tracker.feature_applications.domain.use_cases

import com.studbudd.application_tracker.core.data.models.Resource
import com.studbudd.application_tracker.feature_applications.data.repo.JobApplicationsRepository

class UpdateJobApplicationUseCase(
    private val repo: JobApplicationsRepository,
    private val handlePeriodicNotification: HandlePeriodicNotification
) {
    suspend operator fun invoke(id: Long, notes: String?, status: Long): Resource<Unit> {
        return when (val res = repo.updateApplication(id = id, notes = notes, status = status)) {
            is Resource.Success -> {
                handlePeriodicNotification(res.data!!)
                Resource.Success(Unit, "Application updated successfully")
            }
            is Resource.Failure -> Resource.Failure(res.message)
            is Resource.LoggedOut -> Resource.LoggedOut()
        }
    }
}