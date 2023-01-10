package com.studbudd.application_tracker.feature_applications_management.domain.use_cases

import com.studbudd.application_tracker.common.data.models.Resource
import com.studbudd.application_tracker.feature_applications_management.data.entity.JobApplication
import com.studbudd.application_tracker.feature_applications_management.data.repo.JobApplicationsRepository
import javax.inject.Inject

class AddJobApplicationUseCase (
    private val repo: JobApplicationsRepository
) {

    suspend operator fun invoke(
        companyName: String,
        role: String,
        jobLink: String,
        notes: String?,
        status: Int
    ): Resource<Boolean> {
        val res = repo.addJobApplication(
            JobApplication(
                companyName = companyName,
                role = role,
                jobLink = jobLink,
                notes = notes,
                status = status
            )
        )
        return if (res is Resource.Success)
            Resource.Success(true)
        else Resource.Failure(res.message)
    }

}