package com.studbudd.application_tracker.feature_applications.domain.use_cases

import com.studbudd.application_tracker.feature_applications.data.repo.JobApplicationsRepository

class GetJobApplicationsUseCase(
    private val repo: JobApplicationsRepository
) {

    suspend operator fun invoke(pageSize: Int, pageNum: Int) =
        repo.getApplications(pageSize, pageNum)

}